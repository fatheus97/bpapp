package components;

import dbModel.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutputMaker {
    private static Document doc;
    private static int width;
    private static int height;
    private static final Properties props = new Properties();
    static {
        try {
            props.load(NetworkUtil.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final int radius = Integer.parseInt(props.getProperty("heatmap.radius"));

    public static Path makeHTMLOutput(Organisation organisation) throws IOException, ArrayIndexOutOfBoundsException {
        // create a new HTML document
        doc = Document.createShell("");
        Element head = doc.head();

        doc.head().append("""
            <!-- Load c3.css -->
            <link href="../js/c3/c3.css" rel="stylesheet">
    
            <!-- Load d3.js and c3.js -->
            <script src="../js/d3/d3.min.js" charset="utf-8"></script>
            <script src="../js/c3/c3.min.js"></script>""");

        Element body = doc.body();

        // add a title to the document
        head.appendElement("title").text("Prep sheet for match vs " + organisation.getName());

        // add a header to the document
        Element h1 = body.appendElement("h1");
        h1.text("Prep sheet for match vs " + organisation.getName());

        Roster roster = organisation.getLastRoster();

        addInfographics(roster);
        addHeatmaps(roster);
        addGraphs(roster);

        Path filePath = Paths.get("output/LoLPrep.html");
        Files.createDirectories(filePath.getParent());

        // write the HTML document to the temporary file
        BufferedWriter writer = Files.newBufferedWriter(filePath);
        writer.write(doc.toString());

        writer.close();

        return filePath;
    }

    private static void addGraphs(Roster roster) {
        long[] gold = new long[100];
        Player player = roster.getPlayers().get(0);
        roster.getMatches().forEach(match -> {
            Optional<Long> participantID = match.getInfo().getParticipants().stream().filter(participant -> Objects.equals(participant.getSummonerName(), roster.getOrg().getShortcut() + " " + player.getName())).map(Participant::getParticipantId).findFirst();
            if (participantID.isPresent())
                match.getTimeline().getFrames().forEach(frame -> frame.getParticipantFrames().forEach((s, participantFrame) -> {
                    if (participantID.get() <= 5) {
                        if (Integer.parseInt(s) <= 5) {
                            gold[(int) (frame.getTimestamp()/60000)] += participantFrame.getCurrentGold();
                        }
                    } else {
                        if (Integer.parseInt(s) > 5) {
                            gold[(int) (frame.getTimestamp()/60000)] += participantFrame.getCurrentGold();
                        }
                    }
                }));
        });

        String goldData = Arrays.stream(gold).filter(value -> value != 0).mapToObj(Long::toString).collect(Collectors.joining(","));

        doc.body().appendElement("div").attr("id","goldChart");
        doc.body().appendElement("script").append("var chart = c3.generate({\n" +
                "    bindto: '#goldChart',\n" +
                "    data: {\n" +
                "      columns: [\n" +
                "        ['gold'," + goldData + "],\n" +
                "      ]\n" +
                "    }\n" +
                "});");
    }

    private static void addHeatmaps(Roster roster) throws IOException, ArrayIndexOutOfBoundsException {
        Optional<Player> optionalPlayer = roster.getPlayers().stream().filter(player -> player.getRole() == Role.JUNGLE).findFirst();
        if (optionalPlayer.isPresent())
            addHeatmap(optionalPlayer.get(), roster);
        optionalPlayer = roster.getPlayers().stream().filter(player -> player.getRole() == Role.SUPPORT).findFirst();
        if (optionalPlayer.isPresent())
            addHeatmap(optionalPlayer.get(), roster);
    }

    private static void addHeatmap(Player player, Roster roster) throws IOException, ArrayIndexOutOfBoundsException {
        BufferedImage image = ImageIO.read(new File("src/main/resources/background.png"));
        width = image.getWidth();
        height = image.getHeight();
        int[][] heatmap = new int[width][height];
        Graphics2D g2d = image.createGraphics();

        addSoloQFrames(player, heatmap);
        addCompetitiveFrames(roster, heatmap);

        drawHeatmap(heatmap, image);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 1f));
        BufferedImage mapImage = ImageIO.read(new File("src/main/resources/background.png"));
        g2d.drawImage(mapImage, 0, 0, null);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        BufferedImage wallsImage = ImageIO.read(new File("src/main/resources/walls.png"));
        g2d.drawImage(wallsImage, 0, 0, null);
        BufferedImage borderImage = ImageIO.read(new File("src/main/resources/border.png"));
        g2d.drawImage(borderImage, 0, 0, null);

        g2d.dispose();

        ImageIO.write(image, "png", new File("output/" + player.getName() + "_heatmap.png"));

        Path filePath = Paths.get("output/" + player.getName() + "_heatmap.png");

        doc.body().appendElement("img").attr("src", filePath.toUri().toURL().toString()).attr("style","max-width:100%;");
    }

    private static void addCompetitiveFrames(Roster roster, int[][] heatmap) throws ArrayIndexOutOfBoundsException {
        roster.getMatches().forEach(match -> {
            String summonerName = roster.getOrg().getShortcut() + " " + roster.getPlayers().stream().filter(player1 -> player1.getRole().equals(Role.TOP)).map(Player::getName).findFirst().get();
            Optional<Long> participantID = match.getInfo().getParticipants().stream().filter(participant -> participant.getSummonerName().equals(summonerName)).map(Participant::getParticipantId).findFirst();
            participantID.ifPresent(aLong -> match.getTimeline().getFrames().forEach(frame -> {
                ParticipantFrame participantFrame = frame.getParticipantFrames().get(aLong.toString());
                drawCircle(participantFrame, heatmap);
            }));
        });
    }

    private static void addSoloQFrames(Player player, int[][] heatmap) throws ArrayIndexOutOfBoundsException {
        player.getAccounts().forEach(account -> {
            String puuid = account.getPuuid();
            account.getMatches().forEach(match -> {
                Optional<Long> participantID = match.getInfo().getParticipants().stream()
                        .filter(participant -> participant.getPuuid().equals(puuid))
                        .map(Participant::getParticipantId).findFirst();
                participantID.ifPresent(aLong -> match.getTimeline().getFrames().forEach(frame -> {
                    frame.getParticipantFrames().forEach((s, participantFrame) -> {
                        if (participantFrame.getParticipantId() == aLong) {
                            drawCircle(participantFrame, heatmap);
                        }
                    });
                }));
            });
        });
    }

    private static void drawCircle(ParticipantFrame participantFrame, int[][] heatmap) throws ArrayIndexOutOfBoundsException{
        int x = (int) participantFrame.getPosition().getX()/10;
        int y = height - (int) participantFrame.getPosition().getY()/10;
        if ((x<1350||y>150)&&(x>150||y<1350)) {
            int m = 0;
            while (2 + 2 * m < (radius - 1 - 2 * m) * Math.sqrt(2)) {
                m++;
            }
            for (int i = m; i < radius * 2 + 1 - m; i++) {
                if (i > radius - m && i < radius + m) {
                    for (int j = m + i - radius; j < i * 2 + 1 - (m + i - radius); j++) {
                        heatmap[x - i + j][y - radius + i]++;
                    }
                } else if (i <= radius) {
                    for (int j = 0; j < i * 2 + 1; j++) {
                        heatmap[x - i + j][y - radius + i]++;
                    }
                } else {
                    for (int j = 0; j < 4 * radius + 1 - 2 * i; j++) {
                        heatmap[x + i - 2 * radius + j][y - radius + i]++;
                    }
                }
            }
        }
    }

    private static void drawHeatmap(int[][] heatmap, BufferedImage image) {
        int max = Arrays.stream(heatmap).flatMapToInt(Arrays::stream).max().getAsInt();
        if (max>0) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, new Color(heatmap[i][j] * (255 / max), 255 - heatmap[i][j] * (255 / max), 150 - heatmap[i][j] * (150 / max)).getRGB());
                }
            }
        }
    }

    private static void addInfographics(Roster roster) {
        addCompetitiveChampPool(roster);
        addSoloQChampPool(roster);
    }

    private static void addSoloQChampPool(Roster roster) {
        doc.body().appendElement("h2").text("SoloQ Champ pool");
        Element div = doc.body().appendElement("div").attr("style", "display:flex;align-items: flex-start");
        roster.getPlayers().forEach(player -> {
            Element table = div.appendElement("table");
            Element trh = table.appendElement("tr");
            trh.appendElement("th").text(player.getName()).attr("colspan", "2");
            getSoloQChampPool(player).forEach((k,v) -> {
                Element trd = table.appendElement("tr");
                trd.appendElement("td").text(k);
                trd.appendElement("td").text(String.valueOf(v));
            });
        });
    }

    private static void addCompetitiveChampPool(Roster roster) {
        doc.body().appendElement("h2").text("Competitive Champ pool");
        Element div = doc.body().appendElement("div").attr("style", "display:flex;align-items: flex-start");
        roster.getPlayers().forEach(player -> {
            Element table = div.appendElement("table");
            Element trh = table.appendElement("tr");
            trh.appendElement("th").text(player.getName()).attr("colspan", "2");
            getCompetitiveChampPool(roster, player).forEach((k,v) -> {
                Element trd = table.appendElement("tr");
                trd.appendElement("td").text(k);
                trd.appendElement("td").text(String.valueOf(v));
            });
        });
    }

    private static Map<String, Integer> getCompetitiveChampPool(Roster roster, Player player) {
        Map<String, Integer> champCount = new HashMap<>();
        roster.getMatches().forEach(match -> match.getInfo().getParticipants()
                        .forEach(participant -> {
                            if(participant.getSummonerName().equals(roster.getOrg().getShortcut() + " " + player.getName())){
                                addChampCount(participant.getChampionName(), champCount);
                            }
                        }));

        return champCount.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static Map<String, Integer> getSoloQChampPool(Player player) {
        Map<String, Integer> champCount = new HashMap<>();
        player.getAccounts().forEach(account -> account.getMatches()
                .forEach(match -> match.getInfo().getParticipants()
                        .forEach(participant -> {
                            if(participant.getSummonerName().equals(account.getName())){
                                addChampCount(participant.getChampionName(), champCount);
                            }
                        })));

        return champCount.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static void addChampCount(String champName, Map<String, Integer> champCount) {
        int count = champCount.getOrDefault(champName, 0);
        champCount.put(champName, ++count);
    }

    /* static void evaluateMatches(Organization org) {
        int nOfChanges = org.getLastRoster().getNOfChanges();
        org.getLastRoster().getPlayers().forEach(player -> {
            player.getAccounts().forEach(account -> {
                boolean competitive = account.isCompetitive();
                account.getMatches().forEach(match -> {
                    if(competitive)
                        match.setValue(500-(100*nOfChanges));
                    else
                        match.setValue(100);
                });
            });
        });
    }*/
}
