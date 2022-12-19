package AmmyRQ.Home.Commands;

import AmmyRQ.Home.Home;
import AmmyRQ.Home.LanguageManager;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class HomeCommand extends Command
{

    public HomeCommand(String name)
    {
        super(name, "Home main command", LanguageManager.getTranslation("wrongCmdFormat"));
        this.commandParameters.clear();
        this.commandParameters.put("option", new CommandParameter[]{
            CommandParameter.newEnum("type", new CommandEnum("options", new String[]{"add", "remove", "tp"})),
            CommandParameter.newType("name", CommandParamType.TEXT)
        });
        this.commandParameters.put("optionList", new CommandParameter[]{
                CommandParameter.newEnum("list", new String[]{"list"})
        });
        this.commandParameters.put("optionHelp", new CommandParameter[]{
            CommandParameter.newEnum("help", new String[]{"help"})
        });
    }

    /**
     * @param sender Command sender
     * @param commandLabel  Command label
     * @param args  Command arguments
     * @return boolean
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        Player player = (Player)sender;

        if(args.length < 1)
        {
            player.sendMessage(LanguageManager.getTranslation("wrongCmdFormat"));
            return false;
        }

        //Creates player's file if not exists
        File file = new File(Home.getInstance().getDataFolder(), "/players/" + player.getName() + ".yml");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            Home.getInstance().getLogger().debug(player.getName() + "'s file has been created successfully.");
        }

        //Switch to subcommands
        switch(args[0])
        {
            case "add":
            case "create":
            case "new":
                if(args.length < 2)
                {
                    player.sendMessage(LanguageManager.getTranslation("wrongSubCmdFormat"));
                    return false;
                }

                if(args[1].isBlank())
                {
                    player.sendMessage(LanguageManager.getTranslation("addHome-EmptyName"));
                    return false;
                }

                this.addHome(player, args[1]);
            break;

            case "del":
            case "delete":
            case "remove":
                if(args.length < 2)
                {
                    player.sendMessage(LanguageManager.getTranslation("wrongSubCmdFormat"));
                    return false;
                }

                if(args[1].isBlank())
                {
                    player.sendMessage(LanguageManager.getTranslation("addHome-EmptyName"));
                    return false;
                }

                this.delHome(player, args[1]);
            break;

            case "teleport":
            case "tp":
                if(args.length < 2)
                {
                    player.sendMessage(LanguageManager.getTranslation("wrongSubCmdFormat"));
                    return false;
                }

                if(args[1].isBlank())
                {
                    player.sendMessage(LanguageManager.getTranslation("addHome-EmptyName"));
                    return false;
                }

                this.tpHome(player, args[1]);
            break;

            case "list":
            case "l":
                this.listHomes(player);
            break;

            case "help":
            case "?":
            case "h":
                player.sendMessage(
                    LanguageManager.getTranslation("homeHelp")
                        .replace("{version}", Home.getInstance().getDescription().getVersion()
                    )
                );
            break;
        }


        return true;
    }

    /**
     * Add home subcommand
     * @param player    Player object
     * @param name      Home name
     */
    private void addHome(Player player, String name)
    {
        //Player's file
        Config playerFile = new Config(Home.getInstance().getDataFolder() + "/players/" + player.getName() + ".yml");

        if(playerFile.exists(name))
        {
            player.sendMessage(LanguageManager.getTranslation("addHome-HomeAlreadyExists"));
            return;
        }

        HashMap<Object, Object> coords = new HashMap<>() {
            {
                put("x", player.getX());
                put("y", player.getY());
                put("z", player.getZ());
                put("world", player.getLevel().getName());
            }
        };

        for(Object key : coords.keySet())
        {
            playerFile.set(name + "." + key, coords.get(key));
        }

        playerFile.save(true);

        player.sendMessage(
            LanguageManager.getTranslation("addHome-Success").replace("{name}", name)
        );
    }

    /**
     * Remove home subcommand
     * @param player    Player object
     * @param name      Home name
     */
    private void delHome(Player player, String name)
    {
        //Player's file
        Config playerFile = new Config(Home.getInstance().getDataFolder() + "/players/" + player.getName() + ".yml");

        if(!playerFile.exists(name))
        {
            player.sendMessage(LanguageManager.getTranslation("HomeDoesNotExists"));
            return;
        }

        playerFile.remove(name);
        playerFile.save();

        player.sendMessage(LanguageManager.getTranslation("delHome-Success"));
    }

    /**
     * Teleport home subcommand
     * @param player    Player object
     * @param name      Home name
     */
    private void tpHome(Player player, String name)
    {
        //Player's file
        Config playerFile = new Config(Home.getInstance().getDataFolder() + "/players/" + player.getName() + ".yml");

        if(!playerFile.exists(name))
        {
            player.sendMessage(LanguageManager.getTranslation("HomeDoesNotExists"));
            return;
        }

        double x = playerFile.getDouble(name + ".x");
        double y = playerFile.getDouble(name + ".y");
        double z = playerFile.getDouble(name + ".z");
        Level world = Home.getInstance().getServer().getLevelByName(playerFile.getString(name + ".world"));

        player.teleport(new Position(x, y, z, world));
        player.sendMessage(LanguageManager.getTranslation("tpHome-Success"));
    }

    /**
     * List homes subcommand
     * @param player    Player object
     */
    private void listHomes(Player player)
    {
        Config playerFile = new Config(Home.getInstance().getDataFolder() + "/players/" + player.getName() + ".yml");

        if(playerFile.getAll().isEmpty())
        {
            player.sendMessage(LanguageManager.getTranslation("noHomes"));
            return;
        }

        player.sendMessage(LanguageManager.getTranslation("listHomes"));

        for(String key : playerFile.getAll().keySet())
            player.sendMessage(LanguageManager.getTranslation("homeInList").replace("{name}", key));
    }
}
