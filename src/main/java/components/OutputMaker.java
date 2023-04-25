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

public class OutputMaker {
    private static Element container;
    private static int width;
    private static int height;
    private static final Properties PROPS = new Properties();

    static {
        try {
            PROPS.load(NetworkUtil.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final int RADIUS = Integer.parseInt(PROPS.getProperty("heatmap.radius"));

    private OutputMaker() {
    }

    public static Path makeHTMLOutput(Organisation organisation) throws IOException, ArrayIndexOutOfBoundsException {
        Document doc = Document.createShell("");
        Element head = doc.head();

        head.append("""
                <!-- Load c3.css -->
                <link href="../js/c3/c3.css" rel="stylesheet">
                    
                <!-- Load d3.js and c3.js -->
                <script src="../js/d3/d3.min.js" charset="utf-8"></script>
                <script src="../js/c3/c3.min.js"></script>""");

        head.appendElement("title").text("Prep sheet for match vs " + organisation.getName());
        head.appendElement("link").attr("rel", "icon")
                .attr("href", "../src/main/resources/icon.png")
                .attr("sizes", "128x128")
                .attr("type", "image/png");
        head.appendElement("link").attr("rel", "stylesheet")
                .attr("href", "../src/main/resources/styles.css");

        container = doc.createElement("div");
        container.addClass("container");
        container.appendElement("div").attr("id", "background");
        doc.body().appendChild(container);

        Element h1 = container.appendElement("h1");
        h1.text("Prep sheet for match vs " + organisation.getName());

        Roster roster = organisation.getLastRoster();

        Path filePath = Paths.get("output/LoLPrep.html");
        Files.createDirectories(filePath.getParent());

        addInfographics(roster);
        addGraphs(roster);
        addHeatmaps(roster);

        BufferedWriter writer = Files.newBufferedWriter(filePath);
        writer.write(doc.toString());

        writer.close();

        return filePath;
    }

    private static void addGraphs(Roster roster) {
        addGoldGraph(roster);
    }

    private static void addGoldGraph(Roster roster) {
        container.appendElement("h2").text("Gold graph of " + roster.getOrg().getName() + " competitive matches");
        long[] gold = new long[100];
        long NofMatches = roster.getMatches().size();
        if (NofMatches > 0) {
            roster.getMatches().forEach(match -> {
                boolean blue = isBlue(roster, match);
                match.getTimeline().getFrames().forEach(frame -> frame.getParticipantFrames().forEach((s, participantFrame) -> {
                    if (blue) {
                        if (Integer.parseInt(s) <= 5) {
                            gold[(int) (frame.getTimestamp() / 60000)] += participantFrame.getTotalGold();
                        }
                    } else {
                        if (Integer.parseInt(s) > 5) {
                            gold[(int) (frame.getTimestamp() / 60000)] += participantFrame.getTotalGold();
                        }
                    }
                }));
            });


            String goldData = Arrays.stream(gold).map(value -> value / NofMatches).filter(value -> value != 0).mapToObj(Long::toString).collect(Collectors.joining(","));

            container.appendElement("div").attr("id", "goldChart");
            container.appendElement("script").append("var chart = c3.generate({\n" +
                    "    bindto: '#goldChart',\n" +
                    "    data: {\n" +
                    "      columns: [\n" +
                    "        ['gold'," + goldData + "],\n" +
                    "      ]\n" +
                    "    }\n" +
                    "});");
        }
    }

    private static boolean isBlue(Roster roster, Match match) {
        Player player = roster.getPlayers().get(1);
        Optional<Long> participantID = match.getInfo().getParticipants().stream().filter(participant -> Objects.equals(participant.getSummonerName(), roster.getOrg().getShortcut() + " " + player.getName())).map(Participant::getParticipantId).findFirst();
        if (participantID.isPresent())
            return participantID.get() <= 5;
        else {
            System.out.println(match.getMatchID() + " of " + roster.getOrg().getName());
            return true;
        }
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
        container.appendElement("h2").text("Heatmap of " + player.getRole() + " (" + player.getName() + ")");
        BufferedImage image = ImageIO.read(new File("src/main/resources/background.png"));
        width = image.getWidth();
        height = image.getHeight();
        int[][] heatmap = new int[width][height];

        addSoloQFrames(player, heatmap);
        addCompetitiveFrames(roster, heatmap);

        drawHeatmap(heatmap, image);

        Graphics2D g2d = image.createGraphics();
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

        container.appendElement("img").attr("src", filePath.toUri().toURL().toString()).addClass("image");
    }

    private static void addCompetitiveFrames(Roster roster, int[][] heatmap) throws ArrayIndexOutOfBoundsException {
        roster.getMatches().forEach(match -> {
            String summonerName = roster.getOrg().getShortcut() + " " + roster.getPlayers().stream().filter(player1 -> player1.getRole().equals(Role.TOP)).map(Player::getName).findFirst().get();
            Optional<Long> participantID = match.getInfo().getParticipants().stream().filter(participant -> participant.getSummonerName().equals(summonerName)).map(Participant::getParticipantId).findFirst();
            participantID.ifPresent(aLong -> match.getTimeline().getFrames().forEach(frame -> {
                ParticipantFrame participantFrame = frame.getParticipantFrames().get(aLong.toString());
                drawCircle(participantFrame, heatmap, 5);
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
                participantID.ifPresent(aLong -> match.getTimeline().getFrames().forEach(frame -> frame.getParticipantFrames().forEach((s, participantFrame) -> {
                    if (participantFrame.getParticipantId() == aLong) {
                        drawCircle(participantFrame, heatmap, 1);
                    }
                })));
            });
        });
    }

    private static void drawCircle(ParticipantFrame participantFrame, int[][] heatmap, int value) throws ArrayIndexOutOfBoundsException {
        int x = (int) participantFrame.getPosition().getX() / 10;
        int y = height - (int) participantFrame.getPosition().getY() / 10;
        if ((x < 1350 || y > 150) && (x > 150 || y < 1350)) {
            int m = 0;
            while (2 + 2 * m < (RADIUS - 1 - 2 * m) * Math.sqrt(2)) {
                m++;
            }
            for (int i = m; i < RADIUS * 2 + 1 - m; i++) {
                if (i > RADIUS - m && i < RADIUS + m) {
                    for (int j = m + i - RADIUS; j < i * 2 + 1 - (m + i - RADIUS); j++) {
                        heatmap[x - i + j][y - RADIUS + i] = heatmap[x - i + j][y - RADIUS + i] + value;
                    }
                } else if (i <= RADIUS) {
                    for (int j = 0; j < i * 2 + 1; j++) {
                        heatmap[x - i + j][y - RADIUS + i] = heatmap[x - i + j][y - RADIUS + i] + value;
                    }
                } else {
                    for (int j = 0; j < 4 * RADIUS + 1 - 2 * i; j++) {
                        heatmap[x + i - 2 * RADIUS + j][y - RADIUS + i] = heatmap[x + i - 2 * RADIUS + j][y - RADIUS + i] + value;
                    }
                }
            }
        }
    }

    private static void drawHeatmap(int[][] heatmap, BufferedImage image) {
        int max = Arrays.stream(heatmap).flatMapToInt(Arrays::stream).max().getAsInt();
        if (max > 0) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, new Color(heatmap[i][j] * (255 / max), 255 - heatmap[i][j] * (255 / max), 150 - heatmap[i][j] * (150 / max)).getRGB());
                }
            }
        }
    }

    private static void addInfographics(Roster roster) {
        addCompetitiveGameRecord(roster);
        addCompetitiveChampPool(roster);
        addSoloQChampPool(roster);
    }

    private static void addCompetitiveGameRecord(Roster roster) {
        container.appendElement("h2").text("Competitive Game Record");
        Element div = container.appendElement("div").attr("style", "display: flex; align-items: flex-start");
        roster.getMatches().forEach(match -> {
            boolean blue = isBlue(roster, match);
            boolean win;
            if (blue)
                win = match.getInfo().getTeams().stream().filter(team -> team.getTeamId() == 100).findFirst().get().getWin();
            else
                win = match.getInfo().getTeams().stream().filter(team -> team.getTeamId() == 200).findFirst().get().getWin();
            if (win)
                div.appendElement("div").text("W").attr("style", "background-color: lawngreen;font-size: 32px;padding: 4px;border:1px solid #ccc;");
            else
                div.appendElement("div").text("L").attr("style", "background-color: red;font-size: 32px;padding: 4px;border:1px solid #ccc;");
        });
    }

    private static void addSoloQChampPool(Roster roster) {
        container.appendElement("h2").text("SoloQ Champ pool");
        Element div = container.appendElement("div").attr("style", "display: flex; align-items: flex-start");
        roster.getPlayers().forEach(player -> {
            Element table = div.appendElement("table");
            Element trIcon = table.appendElement("tr").attr("style", "background-color: #E7E9EB; border-bottom: 1px solid #ddd;border-collapse: collapse; border-spacing: 0;");
            trIcon.appendElement("th").attr("colspan", "2").appendElement("img").attr("src", "../src/main/resources/" + player.getRole() + ".png");
            Element trh = table.appendElement("tr").attr("style", "background-color: #E7E9EB; border-bottom: 1px solid #ddd;border-collapse: collapse; border-spacing: 0;");
            trh.appendElement("th").text(player.getName()).attr("colspan", "2");
            getSoloQChampPool(player).forEach((k, v) -> {
                if (v >= 1) {
                    Element trd = table.appendElement("tr").attr("style", "background-color: rgba(255," + (255 - v * 3) + ",0,15);; border-bottom: 1px solid #ddd;border-collapse: collapse; border-spacing: 0;");
                    trd.appendElement("td").text(k).attr("style", "padding-left: 20%;padding: 8px 8px; display: table-cell; text-align: left; vertical-align: top;border-collapse: collapse; border-spacing: 0;");
                    trd.appendElement("td").text(String.valueOf(v)).attr("style", "padding: 8px 8px; display: table-cell; text-align: left; vertical-align: top;border-collapse: collapse; border-spacing: 0;");
                }
            });
        });
    }

    private static void addCompetitiveChampPool(Roster roster) {
        container.appendElement("h2").text("Competitive Champ pool");
        Element div = container.appendElement("div").attr("style", "display:flex;align-items: flex-start");
        roster.getPlayers().forEach(player -> {
            Element table = div.appendElement("table");
            Element trIcon = table.appendElement("tr").attr("style", "background-color: #E7E9EB; border-bottom: 1px solid #ddd;border-collapse: collapse; border-spacing: 0;");
            trIcon.appendElement("th").attr("colspan", "2").appendElement("img").attr("src", "../src/main/resources/" + player.getRole() + ".png");
            Element trh = table.appendElement("tr").attr("style", "padding: 8px 8px; background-color: #E7E9EB; border-bottom: 1px solid #ddd;border-collapse: collapse; border-spacing: 0;");
            trh.appendElement("th").text(player.getName()).attr("colspan", "2");
            getCompetitiveChampPool(roster, player).forEach((k, v) -> {
                Element trd = table.appendElement("tr").attr("style", "background-color: rgba(255," + (255 - v * 3) + ",0,15); border-bottom: 1px solid #ddd;border-collapse: collapse; border-spacing: 0;");
                trd.appendElement("td").text(k).attr("style", "padding: 8px 8px; display: table-cell; text-align: left; vertical-align: top;border-collapse: collapse; border-spacing: 0;");
                trd.appendElement("td").text(String.valueOf(v)).attr("style", "padding: 8px 8px; display: table-cell; text-align: left; vertical-align: top;border-collapse: collapse; border-spacing: 0;");
            });
        });
    }

    private static Map<String, Integer> getCompetitiveChampPool(Roster roster, Player player) {
        Map<String, Integer> champCount = new HashMap<>();
        roster.getMatches().forEach(match -> match.getInfo().getParticipants()
                .forEach(participant -> {
                    if (participant.getSummonerName().equals(roster.getOrg().getShortcut() + " " + player.getName())) {
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
                            if (participant.getSummonerName().equals(account.getName())) {
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


    // FOR FUTURE DEVELOPMENT
    static void evaluateMatches(Organisation org) {
        //int nOfChanges = org.getLastRoster().getNOfChanges();
        org.getLastRoster().getPlayers().forEach(player -> {
            player.getAccounts().forEach(account -> {
                //boolean competitive = account.isCompetitive();
                account.getMatches().forEach(match -> {
                    //if(competitive)
                    //match.setValue(500-(100*nOfChanges));
                    //else
                    match.setValue(100);
                });
            });
        });
    }
}
