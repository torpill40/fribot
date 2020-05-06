package com.torpill.fribot.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.util.ListBuilder;
import com.vdurmont.emoji.EmojiParser;

/**
 * 
 * Cette classe représente un thread permettant la gestion de l'utilitaire d'aide.
 * 
 * @author torpill40
 *
 */

public class HelpThread extends BotThread {

	private int page;

	/**
	 * 
	 * Constructeur de la classe <code>HelpThread</code>.
	 * 
	 * @param bot
	 *            : bot Discord relié au thread.
	 * 
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public HelpThread(DiscordBot bot) {

		super(bot, "help");
	}

	@Override
	protected List<? extends Class<?>> args() {

		return ListBuilder.listOf(User.class, TextChannel.class, Map.class);
	}

	@Override
	public void run() {

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' démarré avec succès.");

		final User user = (User) this.args[0];
		final TextChannel channel = (TextChannel) this.args[1];
		@SuppressWarnings("unchecked")
		final Map<String, Command> commands = (Map<String, Command>) this.args[2];

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' paramètres récupérés avec succès.");

		final int time = 60;
		final int commandPerPage = 6;
		final List<String> commandNames = new ArrayList<>();
		final EmbedBuilder embed = this.bot.defaultEmbedBuilder("Aide :", "Liste des commandes :", null);
		for (String commandName : commands.keySet()) {

			if (!commandName.startsWith("__")) commandNames.add(commandName);
		}
		final int numberOfPage = Math.floorDiv(commandNames.size() - 1, commandPerPage) + 1;
		this.page = 1;
		embed.setFooter("Page : " + this.page + " / " + numberOfPage, user.getAvatar());

		for (int i = 0; i < commandPerPage; i++) {

			int index = i + (this.page - 1) * commandPerPage;
			if (index < commandNames.size()) {

				String commandName = commandNames.get(index);
				embed.addField(commandName + " :", commands.get(commandName).getHelp().split("\n")[0]);
			}
		}

		try {

			Message message = channel.sendMessage(embed).get();
			message.addReaction(EmojiParser.parseToUnicode(":arrow_left:"));
			message.addReaction(EmojiParser.parseToUnicode(":arrow_right:"));
			ReactionAddListener listener = new ReactionAddListener() {

				@Override
				public void onReactionAdd(ReactionAddEvent event) {

					final User user = event.getUser();
					final Optional<Reaction> optReaction = event.getReaction();
					if (!optReaction.isPresent()) return;
					final Reaction reaction = optReaction.get();
					final Emoji emoji = reaction.getEmoji();
					if (!bot.is(user)) {

						App.LOGGER.debug("Reaction '" + EmojiParser.parseToAliases(emoji.getMentionTag()) + "' added !");

						reaction.removeUser(user);
						if (EmojiParser.parseToAliases(emoji.getMentionTag()).equals(":arrow_right:")) HelpThread.this.page++;
						else if (EmojiParser.parseToAliases(emoji.getMentionTag()).equals(":arrow_left:")) HelpThread.this.page--;
						else return;

						if (HelpThread.this.page > numberOfPage) {

							HelpThread.this.page = 1;

						} else if (HelpThread.this.page < 1) {

							HelpThread.this.page = numberOfPage;
						}

						final EmbedBuilder embed = HelpThread.this.bot.defaultEmbedBuilder("Aide", "Liste des commandes :", null);
						embed.setFooter("Page : " + HelpThread.this.page + " / " + numberOfPage, user.getAvatar());

						for (int i = 0; i < commandPerPage; i++) {

							int index = i + (HelpThread.this.page - 1) * commandPerPage;
							if (index < commandNames.size()) {

								String commandName = commandNames.get(index);
								embed.addField(commandName + " :", commands.get(commandName).getHelp().split("\n")[0]);
							}
						}
						event.editMessage(embed);
					}
				}
			};

			message.addReactionAddListener(listener);

			Thread.sleep(time * 1000);

			message.removeListener(ReactionAddListener.class, listener);

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		super.run();
	}
}
