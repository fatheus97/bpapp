import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import java.util.concurrent.TimeUnit;

public class DataExtractorWiki {
    public static void main(String[] args) {
        MediaWikiBot wikiBot = new MediaWikiBot("https://lol.fandom.com/w/");
        Article article = wikiBot.getArticle("Faker");
        System.out.println(article.getText());
    }
}
