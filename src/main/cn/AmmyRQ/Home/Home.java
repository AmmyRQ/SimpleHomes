package AmmyRQ.Home;

import AmmyRQ.Home.Commands.HomeCommand;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;

public class Home extends PluginBase
{

    private static Home instance;

    @Override
    public void onEnable()
    {
        instance = this;

        this.verifyFiles();

        //Registers the command
        this.getServer().getCommandMap().register("", new HomeCommand("home"));

        this.getServer().getLogger().info("> Home by AmmyRQ enabled successfully.");
    }

    /**
     * @return Home
     */
    public static Home getInstance()
    {
        return instance;
    }

    /**
     * Verifies the existence of the plugin files. If they do not exist, they will be created
     */
    private void verifyFiles()
    {
        this.getDataFolder().mkdir();

        new File(this.getDataFolder(), "/players").mkdir();

        //Default config
        File cfgF = new File(this.getDataFolder(), "/config.yml");
        if(!cfgF.exists())
        {
            this.saveResource("config.yml", false);
            this.getLogger().debug("[Home] Default config file generated successfully.");
        }

        //Languages
        File langFolder = new File(this.getDataFolder()+ "/languages");
        if(langFolder.mkdir())
            this.getLogger().debug("[Home] Lang directory created successfully.");

        LanguageManager.verifyLanguageFiles();
    }
}
