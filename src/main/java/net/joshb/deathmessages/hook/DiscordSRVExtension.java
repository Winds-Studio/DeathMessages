package net.joshb.deathmessages.hook;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.joshb.discordbotapi.server.DiscordBotAPI;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

public class DiscordSRVExtension {

    public DiscordSRVExtension() {

    }


    public void sendDiscordMessage(PlayerManager pm, MessageType messageType, String message) {
        List<String> channels = DiscordAssets.getInstance().getIDs(messageType);
        for (String groups : channels) {
            if (!groups.contains(":")) {
                continue;
            }
            String[] groupSplit = groups.split(":");
            String guildID = groupSplit[0];
            String channelID = groupSplit[1];
            if (DiscordUtil.getJda().getGuildById(guildID) == null) {
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not find the discord guild with ID: " + guildID);
                continue;
            }
            Guild g = DiscordUtil.getJda().getGuildById(guildID);
            if (g.getTextChannelById(channelID) == null) {
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not find the discord text channel with ID: "
                        + channelID + " in guild: " + g.getName());
                continue;
            }
            TextChannel textChannel = g.getTextChannelById(channelID);
            if (getMessages().getBoolean("Discord.DeathMessage.Remove-Plugin-Prefix")
                    && Settings.getInstance().getConfig().getBoolean("Add-Prefix-To-All-Messages")) {
                String prefix = Assets.colorize(getMessages().getString("Prefix"));
                prefix = ChatColor.stripColor(prefix);
                message = message.substring(prefix.length());
            }
            if (getMessages().getString("Discord.DeathMessage.Text").equalsIgnoreCase("")) {
                textChannel.sendMessage(deathMessageToDiscordMessage(pm, message)).queue();
            } else {
                String[] spl = getMessages().getString("Discord.DeathMessage.Text").split("\\\\n");
                StringBuilder sb = new StringBuilder();
                for (String s : spl) {
                    sb.append(s + "\n");
                }
                if (pm.getLastEntityDamager() instanceof FallingBlock) {
                    textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                            null).replaceAll("%message%", message)).queue();
                } else {
                    textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                            (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message)).queue();
                }
            }
        }
    }

    public void sendEntityDiscordMessage(String rawMessage, PlayerManager pm, Entity entity, boolean hasOwner, MessageType messageType) {
        String message = rawMessage;
        List<String> channels = DiscordAssets.getInstance().getIDs(messageType);
        for (String groups : channels) {
            if (!groups.contains(":")) {
                continue;
            }
            String[] groupSplit = groups.split(":");
            String guildID = groupSplit[0];
            String channelID = groupSplit[1];
            if (DiscordBotAPI.getJDA().getGuildById(guildID) == null) {
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not find the discord guild with ID: " + guildID);
                continue;
            }
            Guild g = DiscordUtil.getJda().getGuildById(guildID);
            if (g.getTextChannelById(channelID) == null) {
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not find the discord text channel with ID: "
                        + channelID + " in guild: " + g.getName());
                continue;
            }
            TextChannel textChannel = g.getTextChannelById(channelID);
            if (getMessages().getBoolean("Discord.DeathMessage.Remove-Plugin-Prefix")) {
                String prefix = Assets.colorize(getMessages().getString("Prefix"));
                prefix = ChatColor.stripColor(prefix);
                message = message.substring(prefix.length());
            }
            if (getMessages().getString("Discord.DeathMessage.Text").equalsIgnoreCase("")) {
                textChannel.sendMessage(deathMessageToDiscordMessage(message, pm.getPlayer(), entity, hasOwner)).queue();
            } else {
                String[] spl = getMessages().getString("Discord.DeathMessage.Text").split("\\\\n");
                StringBuilder sb = new StringBuilder();
                for (String s : spl) {
                    sb.append(s + "\n");
                }
                if (pm.getLastEntityDamager() instanceof FallingBlock) {
                    textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                            null).replaceAll("%message%", message)).queue();
                } else {
                    textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                            (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message)).queue();
                }
            }
        }
    }

    public MessageEmbed deathMessageToDiscordMessage(PlayerManager pm, String message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(getDeathMessageColor());
        String name = getMessages().getString("Discord.DeathMessage.Author.Name").replaceAll("%message%", message);
        String url = getMessages().getString("Discord.DeathMessage.Author.URL").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        String iconURL = getMessages().getString("Discord.DeathMessage.Author.Icon-URL").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        if (!url.startsWith("http") && iconURL.startsWith("http")) {
            eb.setAuthor(name, null, iconURL);
        } else if (url.startsWith("http") && !iconURL.startsWith("http")) {
            eb.setAuthor(name, url);
        } else if (!url.startsWith("http") && !iconURL.startsWith("http")) {
            eb.setAuthor(name);
        } else if (name.equalsIgnoreCase("")) {

        } else {
            eb.setAuthor(name, url, iconURL);
        }

        if (getMessages().getString("Discord.DeathMessage.Image").startsWith("http")) {
            eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image").replaceAll("%uuid%",
                    pm.getUUID().toString()).replaceAll("%username%", pm.getName()));
        }
        String title = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Title"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        if (!title.equalsIgnoreCase("")) {
            eb.setTitle(title);
        }
        String description = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        if (!description.equalsIgnoreCase("")) {
            eb.setDescription(description);
        }
        String footerText = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Text"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        String footerIcon = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Icon-URL"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message).replaceAll("%uuid%", pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        if (!footerText.equalsIgnoreCase("") && footerIcon.startsWith("http")) {
            eb.setFooter(footerText, footerIcon);
        } else if (!footerText.equalsIgnoreCase("") && !footerIcon.startsWith("http")) {
            eb.setFooter(footerText);
        }
        boolean timeStamp = getMessages().getBoolean("Discord.DeathMessage.Timestamp");
        if (timeStamp) {
            eb.setTimestamp(Instant.now());
        }
        for (String s : getMessages().getStringList("Discord.DeathMessage.Content")) {
            String[] conSpl = s.split("\\|");
            if (s.startsWith("break")) {
                boolean b = Boolean.parseBoolean(conSpl[1]);
                eb.addBlankField(b);
            } else {
                boolean b = Boolean.parseBoolean(conSpl[2]);
                String header = Assets.playerDeathPlaceholders(conSpl[0], pm,
                        (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
                String subHeader = Assets.playerDeathPlaceholders(conSpl[1], pm,
                        (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
                eb.addField(header, subHeader, b);
            }
        }
        return eb.build();
    }

    public MessageEmbed deathMessageToDiscordMessage(String message, Player p, Entity entity, boolean hasOwner) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(getDeathMessageColor());
        String name = getMessages().getString("Discord.DeathMessage.Author.Name").replaceAll("%message%", message);
        String url = getMessages().getString("Discord.DeathMessage.Author.URL").replaceAll("%uuid%",
                p.getUniqueId().toString()).replaceAll("%username%", p.getName());
        String iconURL = getMessages().getString("Discord.DeathMessage.Author.Icon-URL").replaceAll("%uuid%",
                p.getUniqueId().toString()).replaceAll("%username%", p.getName());
        if (!url.startsWith("http") && iconURL.startsWith("http")) {
            eb.setAuthor(name, null, iconURL);
        } else if (url.startsWith("http") && !iconURL.startsWith("http")) {
            eb.setAuthor(name, url);
        } else if (!url.startsWith("http") && !iconURL.startsWith("http")) {
            eb.setAuthor(name);
        } else if (name.equalsIgnoreCase("")) {

        } else {
            eb.setAuthor(name, url, iconURL);
        }
        if (getMessages().getString("Discord.DeathMessage.Image").startsWith("http")) {
            eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image").replaceAll("%uuid%",
                    p.getUniqueId().toString()).replaceAll("%username%", p.getName()));
        }
        String title = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Title"), p,
                entity, hasOwner).replaceAll("%message%", message);
        if (!title.equalsIgnoreCase("")) {
            eb.setTitle(title);
        }
        String description = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), p,
                entity, hasOwner).replaceAll("%message%", message);
        if (!description.equalsIgnoreCase("")) {
            eb.setDescription(description);
        }
        String footerText = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Text"), p,
                entity, hasOwner).replaceAll("%message%", message);
        String footerIcon = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Icon-URL"), p,
                entity, hasOwner).replaceAll("%message%", message).replaceAll("%uuid%", p.getUniqueId().toString()).replaceAll("%username%", p.getName());
        if (!footerText.equalsIgnoreCase("") && footerIcon.startsWith("http")) {
            eb.setFooter(footerText, footerIcon);
        } else if (!footerText.equalsIgnoreCase("") && !footerIcon.startsWith("http")) {
            eb.setFooter(footerText);
        }
        boolean timeStamp = getMessages().getBoolean("Discord.DeathMessage.Timestamp");
        if (timeStamp) {
            eb.setTimestamp(Instant.now());
        }
        for (String s : getMessages().getStringList("Discord.DeathMessage.Content")) {
            String[] conSpl = s.split("\\|");
            if (s.startsWith("break")) {
                boolean b = Boolean.parseBoolean(conSpl[1]);
                eb.addBlankField(b);
            } else {
                boolean b = Boolean.parseBoolean(conSpl[2]);
                String header = Assets.entityDeathPlaceholders(conSpl[0], p,
                        entity, hasOwner).replaceAll("%message%", message);
                String subHeader = Assets.entityDeathPlaceholders(conSpl[1], p,
                        entity, hasOwner).replaceAll("%message%", message);
                eb.addField(header, subHeader, b);
            }
        }
        return eb.build();
    }

    //Suggested by kuu#3050
    private int getDeathMessageColor() {
        final int color = org.bukkit.Color.BLACK.asRGB();
        try {
            if (getMessages().isColor("Discord.DeathMessage.Color")) {
                return getMessages().getColor("Discord.DeathMessage.Color").asRGB();
            }
            if (getMessages().isString("Discord.DeathMessage.Color")) {
                String colorString = getMessages().getString("Discord.DeathMessage.Color");
                try {
                    return Color.decode(colorString).getRGB();
                } catch (Exception ignored) {
                    org.bukkit.Color c = (org.bukkit.Color) Class.forName("org.bukkit.Color").getField(colorString).get(null);
                    return c.asRGB();
                }
            }
            return getMessages().getInt("Discord.DeathMessage.Color", color);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Error while parsing " + getMessages().getString("Discord.DeathMessage.Color") + " as a color!");
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Make sure your using spigot for your server!");
            return color;
        }
    }

    private FileConfiguration getMessages() {
        return Messages.getInstance().getConfig();
    }
}
