package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.Util;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

public class DiscordSRVExtension {

    public DiscordSRVExtension() {
    }

    public void sendDiscordMessage(TextComponent[] components, MessageType messageType, PlayerCtx playerCtx) {
        final List<String> channels = DiscordAssets.getInstance().getIDs(messageType);

        for (String groups : channels) {
            if (!groups.contains(":")) {
                continue;
            }

            String[] groupSplit = groups.split(":");
            String guildID = groupSplit[0];
            String channelID = groupSplit[1];

            if (DiscordUtil.getJda().getGuildById(guildID) == null) {
                DeathMessages.LOGGER.error("Could not find the discord guild with ID: {}", guildID);
                continue;
            }

            Guild g = DiscordUtil.getJda().getGuildById(guildID);

            if (g.getTextChannelById(channelID) == null) {
                DeathMessages.LOGGER.error("Could not find the discord text channel with ID: {} in guild: {}", channelID, g.getName());
                continue;
            }

            TextChannel textChannel = g.getTextChannelById(channelID);
            String prefix = components[0] != null ? PlainTextComponentSerializer.plainText().serialize(components[0]) : "";
            String messageBody = PlainTextComponentSerializer.plainText().serialize(components[1]);
            String message;

            if (getMessages().getBoolean("Discord.DeathMessage.Remove-Plugin-Prefix")
                    && FileStore.CONFIG.getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES)) {
                message = messageBody;
            } else {
                message = prefix + messageBody;
            }

            // Try to strip Minecraft format code to plain text
            if (message.contains("§")) {
                message = Util.STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
            }

            if (getMessages().getString("Discord.DeathMessage.Text").isEmpty()) {
                textChannel.sendMessage(buildMessage(playerCtx, message)).queue();
            } else {
                String[] spl = getMessages().getString("Discord.DeathMessage.Text").split("\\\\n");
                StringBuilder sb = new StringBuilder();
                for (String s : spl) {
                    sb.append(s + "\n");
                }
                if (playerCtx.getLastEntityDamager() instanceof FallingBlock) {
                    textChannel.sendMessage(Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(sb.toString()), playerCtx, null)
                                    .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build())))
                            .queue();
                } else {
                    textChannel.sendMessage(Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(sb.toString()), playerCtx, playerCtx.getLastEntityDamager())
                                    .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build())))
                            .queue();
                }
            }
        }
    }

    public void sendEntityDiscordMessage(TextComponent[] components, MessageType messageType, PlayerCtx playerCtx, Entity entity, boolean hasOwner) {
        final List<String> channels = DiscordAssets.getInstance().getIDs(messageType);

        for (String groups : channels) {
            if (!groups.contains(":")) {
                continue;
            }

            String[] groupSplit = groups.split(":");
            String guildID = groupSplit[0];
            String channelID = groupSplit[1];

            Guild g = DiscordUtil.getJda().getGuildById(guildID);

            if (g.getTextChannelById(channelID) == null) {
                DeathMessages.LOGGER.error("Could not find the discord text channel with ID: {} in guild: {}", channelID, g.getName());
                continue;
            }

            TextChannel textChannel = g.getTextChannelById(channelID);
            String prefix = components[0] != null ? PlainTextComponentSerializer.plainText().serialize(components[0]) : "";
            String messageBody = PlainTextComponentSerializer.plainText().serialize(components[1]);
            String message;

            if (getMessages().getBoolean("Discord.DeathMessage.Remove-Plugin-Prefix")
                    && FileStore.CONFIG.getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES)) {
                message = messageBody;
            } else {
                message = prefix + messageBody;
            }

            // Try to strip Minecraft format code to plain text
            if (message.contains("§")) {
                message = Util.STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
            }

            if (getMessages().getString("Discord.DeathMessage.Text").isEmpty()) {
                textChannel.sendMessage(buildMessage(message, playerCtx.getPlayer(), entity, hasOwner))
                        .queue();
            } else {
                String[] spl = getMessages().getString("Discord.DeathMessage.Text").split("\\\\n");
                StringBuilder sb = new StringBuilder();
                for (String s : spl) {
                    sb.append(s + "\n");
                }

                if (playerCtx.getLastEntityDamager() instanceof FallingBlock) {
                    textChannel.sendMessage(Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(sb.toString()), playerCtx, null)
                                    .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build())))
                            .queue();
                } else {
                    textChannel.sendMessage(Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(sb.toString()), playerCtx, playerCtx.getLastEntityDamager())
                                    .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build())))
                            .queue();
                }
            }
        }
    }

    private MessageEmbed buildMessage(PlayerCtx playerCtx, String message) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(getDeathMessageColor());

        String name = getMessages().getString("Discord.DeathMessage.Author.Name")
                .replaceAll("%message%", message);
        String url = getMessages().getString("Discord.DeathMessage.Author.URL")
                .replaceAll("%uuid%", playerCtx.getUUID().toString())
                .replaceAll("%username%", playerCtx.getName());
        String iconURL = getMessages().getString("Discord.DeathMessage.Author.Icon-URL")
                .replaceAll("%uuid%", playerCtx.getUUID().toString())
                .replaceAll("%username%", playerCtx.getName());

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
            eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image")
                    .replaceAll("%uuid%", playerCtx.getUUID().toString())
                    .replaceAll("%username%", playerCtx.getName()));
        }

        String title = Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(getMessages().getString("Discord.DeathMessage.Title")), playerCtx, playerCtx.getLastEntityDamager())
                .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build()));

        if (!title.equalsIgnoreCase("")) {
            eb.setTitle(title);
        }

        String description = Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(getMessages().getString("Discord.DeathMessage.Description")), playerCtx, playerCtx.getLastEntityDamager())
                .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build()));

        if (!description.equalsIgnoreCase("")) {
            eb.setDescription(description);
        }

        String footerText = Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(getMessages().getString("Discord.DeathMessage.Footer.Text")), playerCtx, playerCtx.getLastEntityDamager())
                .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build()));
        String footerIcon = Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(getMessages().getString("Discord.DeathMessage.Footer.Icon-URL")), playerCtx, playerCtx.getLastEntityDamager())
                .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build())
                .replaceText(TextReplacementConfig.builder().matchLiteral("%uuid%").replacement(playerCtx.getUUID().toString()).build())
                .replaceText(TextReplacementConfig.builder().matchLiteral("%username%").replacement(playerCtx.getName()).build()));

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
                String header = Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(conSpl[0]), playerCtx, playerCtx.getLastEntityDamager())
                        .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build()));
                String subHeader = Util.convertToLegacy(Assets.playerDeathPlaceholders(Util.convertFromLegacy(conSpl[1]), playerCtx, playerCtx.getLastEntityDamager())
                        .replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(message).build()));
                eb.addField(header, subHeader, b);
            }
        }

        return eb.build();
    }

    private MessageEmbed buildMessage(String message, Player p, Entity entity, boolean hasOwner) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(getDeathMessageColor());

        String name = getMessages().getString("Discord.DeathMessage.Author.Name")
                .replaceAll("%message%", message);
        String url = getMessages().getString("Discord.DeathMessage.Author.URL")
                .replaceAll("%uuid%", p.getUniqueId().toString())
                .replaceAll("%username%", p.getName());
        String iconURL = getMessages().getString("Discord.DeathMessage.Author.Icon-URL")
                .replaceAll("%uuid%", p.getUniqueId().toString())
                .replaceAll("%username%", p.getName());

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
            eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image")
                    .replaceAll("%uuid%", p.getUniqueId().toString())
                    .replaceAll("%username%", p.getName()));
        }

        String title = Util.convertToLegacy(Assets.entityDeathPlaceholders(Util.convertFromLegacy(getMessages().getString("Discord.DeathMessage.Title")), p, entity, hasOwner)
                .replaceText(Util.replace("%message%", message)));

        if (!title.equalsIgnoreCase("")) {
            eb.setTitle(title);
        }

        String description = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), p, entity, hasOwner)
                .replaceAll("%message%", message);

        if (!description.equalsIgnoreCase("")) {
            eb.setDescription(description);
        }

        String footerText = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Text"), p, entity, hasOwner)
                .replaceAll("%message%", message);
        String footerIcon = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Icon-URL"), p, entity, hasOwner)
                .replaceAll("%message%", message)
                .replaceAll("%uuid%", p.getUniqueId().toString())
                .replaceAll("%username%", p.getName());

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
                String header = Assets.entityDeathPlaceholders(conSpl[0], p, entity, hasOwner)
                        .replaceAll("%message%", message);
                String subHeader = Assets.entityDeathPlaceholders(conSpl[1], p, entity, hasOwner)
                        .replaceAll("%message%", message);
                eb.addField(header, subHeader, b);
            }
        }

        return eb.build();
    }

    // Suggested by kuu#3050
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
                } catch (Exception e) {
                    org.bukkit.Color c = (org.bukkit.Color) Class.forName("org.bukkit.Color").getField(colorString).get(null);
                    return c.asRGB();
                }
            }
            return getMessages().getInt("Discord.DeathMessage.Color", color);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            DeathMessages.LOGGER.error("Error while parsing {} as a color!", getMessages().getString("Discord.DeathMessage.Color"));
            DeathMessages.LOGGER.error(e);
            return color;
        }
    }

    private FileConfiguration getMessages() {
        return Messages.getInstance().getConfig();
    }
}
