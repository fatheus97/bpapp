package modules;

import dbModel.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class OutputMaker {
    private static Document doc;
    private static int width;
    private static int height;
    private static final List<Area> areaList = new ArrayList<>();

    public static Path makeHTMLOutput(Organization organization) throws IOException {
        // create a new HTML document
        doc = Document.createShell("");
        Element head = doc.head();
        Element body = doc.body();

        // add a title to the document
        head.appendElement("title").text("Prep sheet for match vs " + organization.getName());

        // add a header to the document
        Element h1 = body.appendElement("h1");
        h1.text("Prep sheet for match vs " + organization.getName());

        Roster roster = organization.getLastRoster();

        addInfographics(roster);
        addHeatmaps(roster);

        Path tempFilePath = getTempFilePath("LOLPrep", ".html");

        // write the HTML document to the temporary file
        try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            writer.write(doc.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempFilePath;
    }

    private static Path getTempFilePath(String prefix, String suffix) {
        Path tempFilePath;
        try {
            tempFilePath = Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempFilePath;
    }

    private static void addHeatmaps(Roster roster) throws IOException {
        Optional<Player> optionalPlayer = roster.getPlayers().stream().filter(player -> player.getRole() == Role.JUNGLE).findFirst();
        if (optionalPlayer.isPresent())
            addJunglersHeatmap(optionalPlayer.get(), roster);
    }

    private static void addJunglersHeatmap(Player player, Roster roster) throws IOException {
        int opacity = 50;
        int circleRadius = 20;

        // Load the background image
        BufferedImage image = ImageIO.read(new File("src/main/resources/background.png"));
        width = image.getWidth();
        height = image.getHeight();
        Graphics2D g2d = image.createGraphics();

        // Draw red circles at specified positions soloq
        player.getAccounts().forEach(account -> {
            String puuid = account.getPuuid();
            account.getMatches().forEach(match -> {
                System.out.println(match);
                Long participantID = match.getInfo().getParticipants().stream()
                        .filter(participant -> participant.getPuuid().equals(puuid))
                        .map(Participant::getParticipantId).findFirst().get();
                match.getTimeline().getFrames().forEach(frame -> {
                    frame.getParticipantFrames().forEach((s, participantFrame) -> {
                        if (participantFrame.getParticipantId() == participantID) {
                            int x = (int) participantFrame.getPosition().getX()/5;
                            int y = (int) participantFrame.getPosition().getY()/5;
                            if ((x>150||y>150)&&(x<2800||y<2800)) {
                                drawCircle(0, new Ellipse2D.Float(x - circleRadius, height - (y - circleRadius), circleRadius * 2, circleRadius * 2));
                            }
                        }
                    });
                    /*frame.getEvents().forEach(event -> {
                        if (Objects.equals(event.getParticipantID(), participantID)) {
                            int x = (int) event.getPosition().getX()/5;
                            int y = (int) event.getPosition().getY()/5;
                            g2d.fill(new Ellipse2D.Float(x-circleRadius, y-circleRadius, circleRadius * 2, circleRadius * 2));
                        }
                    });*/
                });
            });
        });


        roster.getMatches().forEach(match -> {
            String summonerName = roster.getOrg().getShortcut() + " " + roster.getPlayers().stream().filter(player1 -> player1.getRole().equals(Role.TOP)).map(Player::getName).findFirst().get();
            Optional<Long> participantID = match.getInfo().getParticipants().stream().filter(participant -> participant.getSummonerName().equals(summonerName)).map(Participant::getParticipantId).findFirst();
            if (participantID.isPresent()) {
                System.out.println(match);

                match.getTimeline().getFrames().forEach(frame -> {
                    ParticipantFrame participantFrame = frame.getParticipantFrames().get(participantID.get().toString());
                    System.out.println(participantFrame.getPosition().getX() + " " + participantFrame.getPosition().getY());
                    int x = (int) participantFrame.getPosition().getX()/5;
                    int y = (int) participantFrame.getPosition().getY()/5;
                    if ((x>100||y>100)&&(x<2900||y<2900))
                                drawCircle(0, new Ellipse2D.Float(x-circleRadius, height-(y-circleRadius), circleRadius * 2, circleRadius * 2));
                });
                    /*frame.getEvents().forEach(event -> {
                        if (event.getParticipantID() == participantID.get()) {
                            int x = (int) event.getPosition().getX() / 5;
                            int y = (int) event.getPosition().getY() / 5;
                            g2d.fill(new Ellipse2D.Float(x - circleRadius, y - circleRadius, circleRadius * 2, circleRadius * 2));
                        }
                    });*/
            }
        });

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        int size = areaList.size();
        for (int i = 0; i < size; i++) {
            Area area = areaList.get(i);
            g2d.setColor(new Color(1f,0f,0f, ((float)i+1f) / (float)size));
            g2d.fill(area);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        // Load the overlay image
        BufferedImage overlayImage = ImageIO.read(new File("src/main/resources/walls.png"));
        // Draw the overlay image
        g2d.drawImage(overlayImage, 0, 0, null);

        // Dispose the Graphics2D object
        g2d.dispose();

        Path tempFilePath = getTempFilePath("heatmapJungler", ".png");

        // Save the heatmap image to file
        try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            ImageIO.write(image, "png", tempFilePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add the image ref to the HTML
        doc.body().appendElement("img").attr("src", tempFilePath.toUri().toURL().toString()).attr("style","max-width:100%;");
    }

    private static void drawCircle(int i, Shape shape) {
        if (areaList.size() <= i) {
            areaList.add(new Area());
        }
        Area area = areaList.get(i);

        Area intersection = (Area) area.clone();

        area.add(new Area(shape));

        intersection.intersect(new Area(shape));

        if (!intersection.isEmpty()) {
            drawCircle(i+1, intersection);
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
