package AmmyRQ.Home;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;

public class LanguageManager
{

    //Available languages
    private static final ArrayList<String> languages = new ArrayList<>() {
        {
            add("eng"); add("spa"); add("it");
        }
    };

    /**
     * Verifies all language files
     */
    public static void verifyLanguageFiles()
    {
        languages.forEach((lang) -> {
            Home.getInstance().saveResource("languages/" + lang + ".yml", false);
            Home.getInstance().getLogger().debug("[Home] " + lang + ".yml file has been created successfully.");
        });
    }

    /**
     * Returns the current language used by the plugin. Returns "eng" by default
     * @return String
     */
    public static String getLang()
    {
        Config cfg = new Config(Home.getInstance().getDataFolder() + "/config.yml", Config.YAML);

        if(!cfg.exists("language"))
            return "eng";
        else
            return cfg.getString("language");
    }

    /**
     * Gets a string from the language file
     * @param key Key to be translated
     * @return String
     */
    public static String getTranslation(String key)
    {
        Config langFile = new Config(Home.getInstance().getDataFolder() + "/languages/" + getLang() + ".yml", Config.YAML);

        if(!langFile.exists(key))
            return TextFormat.RED + "Key not found. Please, check your language file.";
        else
            return langFile.getString(key);
    }

}
