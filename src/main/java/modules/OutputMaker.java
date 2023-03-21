package modules;

import dbModel.Organization;
import dbModel.Player;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.List;

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

        organization.getRoster().getPlayers().forEach(OutputMaker::addInfographics);

        return doc;
    }

    private static void addInfographics(Player player) {
        doc.body().appendElement("h2").text(player.getName());
        addChampPool(player);

    }

    private static void addChampPool(Player player) {
        Map<String, Integer> champCount = new HashMap<>();
        player.getAccounts().forEach(account -> account.getMatches()
                .forEach(match -> match.getInfo().getParticipants()
                        .forEach(participant -> {
                            if(participant.getSummonerName().equals(account.getName())){
                                addChampCount(participant.getChampionName(), champCount);
                                System.out.println(participant);
                            }
                        })));

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(champCount.entrySet());

        entries.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                int value1 = entry1.getValue();
                int value2 = entry2.getValue();
                if (value1 <= 1 && value2 <= 1) {
                    return 0;
                } else if (value1 <= 1) {
                    return 1;
                } else if (value2 <= 1) {
                    return -1;
                } else {
                    return Integer.compare(value1, value2);
                }
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        Element table = doc.body().appendElement("table");
        Element trh = table.appendElement("tr");
        trh.appendElement("th").text("Champion");
        trh.appendElement("th").text("Occurrence");
        sortedMap.forEach((k,v) -> {
            Element trd = table.appendElement("tr");
            trd.appendElement("td").text(k);
            trd.appendElement("td").text(String.valueOf(v));
        });
    }

    private static void addChampCount(String champName, Map<String, Integer> champCount) {
        int count = champCount.getOrDefault(champName, 0);
        champCount.put(champName, ++count);
    }
}
