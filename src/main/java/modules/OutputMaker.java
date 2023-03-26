package modules;

import dbModel.Organization;
import dbModel.Player;
import dbModel.Roster;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class OutputMaker {
    private static Document doc;

    public static Document makeHTMLOutput(Organization organization) {
        // create a new HTML document
        doc = Document.createShell("");
        Element head = doc.head();
        Element body = doc.body();

        // add a title to the document
        head.appendElement("title").text("Prep sheet for match vs " + organization.getName());

        // add a header to the document
        Element h1 = body.appendElement("h1");
        h1.text("Prep sheet for match vs " + organization.getName());

        addInfographics(organization.getLastRoster());

        return doc;
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
                                System.out.println(participant);
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
                                System.out.println(participant);
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
