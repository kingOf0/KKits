package com.kingOf0.kits.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a minecraft command without plugin.yml
 * https://gist.github.com/redsarow/46a9eb30991bf6007508f72aba7da89f
 *
 * @author redsarow
 */
public abstract class AMyCommand<T extends JavaPlugin> extends Command implements CommandExecutor, PluginIdentifiableCommand {

    private static SimpleCommandMap commandMap;
    private static HashMap<String, Command> knownCommands;
    private static Method syncCommands;

    static {
        Class<? extends Server> server = Bukkit.getServer().getClass();
        try {
            Field commandMapField = server.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            Field field = getDeclaredField(commandMap.getClass(), "knownCommands");
            field.setAccessible(true);
            knownCommands = ((HashMap<String, Command>) field.get(commandMap));
        } catch (IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }

        try {
            syncCommands = server.getDeclaredMethod("syncCommands");
            syncCommands.setAccessible(true);
        } catch (NoSuchMethodException e) {
            syncCommands = null;
        }

    }

    private static Field getDeclaredField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            try {
                Field field = clazz.getSuperclass().getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public final T plugin;
    private final HashMap<Integer, ArrayList<TabCommand>> tabComplete;


    /**
     * @param plugin plugin responsible of the command.
     * @param name   name of the command.
     */
    AMyCommand(T plugin, String name) {
        super(name);

        assert commandMap != null;
        assert plugin != null;
        assert name.length() > 0;

        setLabel(name);
        this.plugin = plugin;
        tabComplete = new HashMap<>();
    }


    //<editor-fold desc="add / set">

    /**
     * @param aliases aliases of the command.
     */
    protected void setAliases(String... aliases) {
        if (aliases != null && aliases.length > 0)
            setAliases(Arrays.stream(aliases).collect(Collectors.toList()));
    }

    //<editor-fold desc="TabbComplete">

    /**
     * Add multiple arguments to an index with permission and words before
     *
     * @param index     index where the argument is in the command. /myCmd is at the index -1, so
     *                   /myCmd index0 index1 ...
     * @param permission permission to add (may be null)
     * @param beforeText text preceding the argument (may be null)
     * @param arg        word to add
     */
    protected void addTabbComplete(int index, String permission, List<TextAvant> beforeText, List<String> arg) {
        if (arg != null && arg.size() > 0 && index >= 0) {
            if (tabComplete.containsKey(index)) {
                tabComplete.get(index).addAll(arg.stream().collect(
                        ArrayList::new,
                        (tabCommands, s) -> tabCommands.add(new TabCommand(index, s, permission, beforeText)),
                        ArrayList::addAll));
            } else {
                tabComplete.put(index, arg.stream().collect(
                        ArrayList::new,
                        (tabCommands, s) -> tabCommands.add(new TabCommand(index, s, permission, beforeText)),
                        ArrayList::addAll)
                );
            }
        }
    }

    /**
     * Add multiple arguments to an index
     *
     * @param indice index where the argument is in the command. /myCmd is at the index -1, so
     *               /myCmd index0 index1 ...
     * @param arg    word to add
     */
    protected void addTabbComplete(int indice, List<String> arg) {
        addTabbComplete(indice, null, null, arg);
    }
    //</editor-fold>
    //</editor-fold>

    /**
     * /!\ to do at the end /!\ to save the command.
     *
     * @return true if the command has been successfully registered
     */
    public boolean register() {
        String command = getName().toLowerCase();
        String pluginName = plugin.getName().toLowerCase();

        PluginCommand pluginCommand = plugin.getCommand(command);
        try {
            for (String alias : getAliases()) {
                String aliasLowerCase = alias.toLowerCase();
                knownCommands.put(aliasLowerCase, pluginCommand);
                knownCommands.put(pluginName + ":" + aliasLowerCase, pluginCommand);
            }
            if (syncCommands != null) {
                syncCommands.invoke(Bukkit.getServer());
                plugin.getLogger().info("CommandManager | Command: '" + getName() + "' registered with 1.19 support.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pluginCommand.setTabCompleter((commandSender, cmd, s, strings) -> tabComplete(commandSender, s, strings));
        pluginCommand.setExecutor(this);
        return true;
    }

    //<editor-fold desc="get">

    /**
     * @return plugin responsible for the command
     */
    @Override
    public T getPlugin() {
        return this.plugin;
    }

    /**
     * @return tabComplete
     */
    public HashMap<Integer, ArrayList<TabCommand>> getTabComplete() {
        return tabComplete;
    }
    //</editor-fold>


    //<editor-fold desc="Override">

    /**
     * @param commandSender sender
     * @param command       command
     * @param arg           argument of the command
     *
     * @return true if ok, false otherwise
     */
    @Override
    public boolean execute(CommandSender commandSender, String command, String[] arg) {
        if (getPermission() != null) {
            if (!commandSender.hasPermission(getPermission())) {
                if (getPermissionMessage() == null) {
                    commandSender.sendMessage(ChatColor.RED + "no permit!");
                } else {
                    commandSender.sendMessage(getPermissionMessage());
                }
                return false;
            }
        }
        if (onCommand(commandSender, this, command, arg))
            return true;
        commandSender.sendMessage(ChatColor.RED + getUsage());
        return false;

    }

    /**
     * @param sender sender
     * @param alias  alias used
     * @param args   argument of the command
     *
     * @return a list of possible values
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

        int indice = args.length - 1;

        if ((getPermission() != null && !sender.hasPermission(getPermission())) || tabComplete.size() == 0 || !tabComplete.containsKey(indice))
            return super.tabComplete(sender, alias, args);


        List<String> list = tabComplete.get(indice).stream()
                .filter(tabCommand -> tabCommand.getPermission() == null || sender.hasPermission(tabCommand.getPermission()))
                .filter(tabCommand -> tabCommand.getTextAvant() == null || tabCommand.getTextAvant().stream().anyMatch(avant -> avant.textAvant.contains(args[avant.index])))
                .map(TabCommand::getText)
                .filter(text -> text.startsWith(args[indice]))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        return list.size() < 1 ? super.tabComplete(sender, alias, args) : list;

    }
    //</editor-fold>

    //<editor-fold desc="class TabCommand">
    private static class TabCommand {

        private final int indice;
        private final String text;
        private final String permission;
        private final List<TextAvant> textAvant;

        private TabCommand(int indice, String text, String permission, List<TextAvant> textAvant) {
            this.indice = indice;
            this.text = text;
            this.permission = permission;
            if (textAvant == null || textAvant.size() < 1) {
                this.textAvant = null;
            }else {
                this.textAvant = textAvant;
            }
        }

        //<editor-fold desc="get&set">
        public String getText() {
            return text;
        }

        public int getIndice() {
            return indice;
        }

        public String getPermission() {
            return permission;
        }

        public List<TextAvant> getTextAvant() {
            return textAvant;
        }
        //</editor-fold>

    }
    public static class TextAvant {
        private final int index;
        private final List<String> textAvant;

        public TextAvant(int index, List<String> textAvant) {
            this.index = index;
            this.textAvant = textAvant;
        }

        public int getIndex() {
            return index;
        }

        public List<String> getTextAvant() {
            return textAvant;
        }
    }
    //</editor-fold>
}