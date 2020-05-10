package com.torpill.fribot.threads;

import java.util.ArrayList;
import java.util.List;
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
 * Cette classe représente un thread permettant la gestion de l'utilitaire
 * d'aide.
 * 
 * @author torpill40
 *
 */

public class HelpThread extends BotThread {

	private int page, numberOfPage, commandPerPage;
	private Command.Category category;
	private List<Command> commandList;
	private List<String> commandNames;

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

		return ListBuilder.listOf(User.class, TextChannel.class);
	}

	@Override
	public void run() {

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' démarré avec succès.");

		final User user = (User) this.args[0];
		final TextChannel channel = (TextChannel) this.args[1];

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' paramètres récupérés avec succès.");

		this.commandPerPage = 6;
		this.category = Command.Category.UTILITY;
		this.commandList = this.bot.commandsIn(this.category);
		this.changeCategory(this.category);

		final EmbedBuilder embed = this.bot.defaultEmbedBuilder("Aide :", this.category.DESCRIPTION + " :", null);
		embed.setFooter("Page : " + this.page + " / " + this.numberOfPage, user.getAvatar());

		for (int i = 0; i < this.commandPerPage; i++) {

			int index = i + (this.page - 1) * this.commandPerPage;
			if (index < this.commandNames.size()) {

				String command = this.commandNames.get(index);
				embed.addField(command + " :", this.bot.getHelpFor(command).split("\n")[0]);
			}
		}

		try {

			Message message = channel.sendMessage(embed).get();
			message.addReaction(EmojiParser.parseToUnicode(":arrow_left:"));
			message.addReaction(EmojiParser.parseToUnicode(":arrow_right:"));
			message.addReaction(EmojiParser.parseToUnicode(":one:"));
			message.addReaction(EmojiParser.parseToUnicode(":two:"));
			message.addReaction(EmojiParser.parseToUnicode(":three:"));
			message.addReaction(EmojiParser.parseToUnicode(":four:"));
			message.addReaction(EmojiParser.parseToUnicode(":five:"));
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

						switch (EmojiParser.parseToAliases(emoji.getMentionTag())) {
						case ":arrow_right:":
							HelpThread.this.next();
							break;

						case ":arrow_left:":
							HelpThread.this.prev();
							break;

						case ":one:":
							HelpThread.this.changeCategory(Command.Category.UTILITY);
							break;

						case ":two:":
							HelpThread.this.changeCategory(Command.Category.FUN);
							break;

						case ":three:":
							HelpThread.this.changeCategory(Command.Category.MODERATION);
							break;

						case ":four:":
							HelpThread.this.changeCategory(Command.Category.GAME);
							break;

						case ":five:":
							HelpThread.this.changeCategory(Command.Category.TUTORIAL);
							break;

						default:
							return;
						}

						final EmbedBuilder embed = HelpThread.this.bot.defaultEmbedBuilder("Aide :", HelpThread.this.category.DESCRIPTION + " :", null);
						embed.setFooter("Page : " + HelpThread.this.page + " / " + HelpThread.this.numberOfPage, user.getAvatar());

						for (int i = 0; i < HelpThread.this.commandPerPage; i++) {

							int index = i + (HelpThread.this.page - 1) * HelpThread.this.commandPerPage;
							if (index < HelpThread.this.commandNames.size()) {

								String command = HelpThread.this.commandNames.get(index);
								embed.addField(command + " :", HelpThread.this.bot.getHelpFor(command).split("\n")[0]);
							}
						}
						event.editMessage(embed);
					}
				}
			};

			message.addReactionAddListener(listener);

			final int time = 60; // En secondes
			Thread.sleep(time * 1000);

			message.removeListener(ReactionAddListener.class, listener);

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		super.run();
	}

	/**
	 * 
	 * Changer de categorie dans l'utilitaire d'aide.
	 * 
	 * @param category
	 *            : nouvelle catégorie.
	 * 
	 * @see com.torpill.fribot.commands.Command.Category
	 */
	private void changeCategory(Command.Category category) {

		if ((this.commandList = this.bot.commandsIn(category)) == null) {

			this.commandList = this.bot.commandsIn(this.category);
			return;
		}

		this.category = category;
		this.commandNames = new ArrayList<>();
		for (Command command : this.commandList) {

			if (!command.getName().startsWith("__")) this.commandNames.add(command.getName());
		}
		this.numberOfPage = Math.floorDiv(this.commandNames.size() - 1, this.commandPerPage) + 1;
		this.page = 1;
	}

	/**
	 * 
	 * Passer à la page suivante.
	 * 
	 */
	private void next() {

		this.page++;
		if (this.page > this.numberOfPage) {

			this.page = 1;
		}
	}

	/**
	 * 
	 * Passer à la page précédente.
	 * 
	 */
	private void prev() {

		this.page--;
		if (this.page < 1) {

			this.page = this.numberOfPage;
		}
	}
}
