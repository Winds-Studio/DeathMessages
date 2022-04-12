package net.joshb.deathmessages.config;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.assets.CommentedConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;

public class PlayerDeathMessages {

    public final String fileName = "PlayerDeathMessages";

    CommentedConfiguration config;

    File file;

    public PlayerDeathMessages(){ }
    private static PlayerDeathMessages instance = new PlayerDeathMessages();
    public static PlayerDeathMessages getInstance(){
        return instance;
    }

    public CommentedConfiguration getConfig(){
        return config;
    }

    public void save(){
        try {
            config.save(file);
        } catch (Exception e){
            File f = new File(DeathMessages.plugin.getDataFolder(), fileName + ".broken." + new Date().getTime());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not save: " + fileName + ".yml");
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: " + f.getName());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(f);
            initialize();
        }
    }

    public void reload(){
        try {
            config = CommentedConfiguration.loadConfiguration(file);
        } catch (Exception e){
            File f = new File(DeathMessages.plugin.getDataFolder(), fileName + ".broken." + new Date().getTime());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not save: " + fileName + ".yml");
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: " + f.getName());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(f);
            initialize();
            e.printStackTrace();
        }
    }

    public void initialize(){
        file = new File(DeathMessages.plugin.getDataFolder(), fileName + ".yml");

        if(!file.exists()){
            file.getParentFile().mkdirs();
            copy(DeathMessages.plugin.getResource(fileName + ".yml"), file);
        }
        config = CommentedConfiguration.loadConfiguration(file);
        try{
            config.syncWithConfig(file, DeathMessages.plugin.getResource(fileName + ".yml"), "Mobs");
        } catch (Exception ignored){

        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
