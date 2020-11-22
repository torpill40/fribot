package com.torpill.fribot.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
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
 * @see com.torpill.fribot.threads.BotThread
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
	public HelpThread(final DiscordBot bot) {

		super(bot, "Help");
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

			final int index = i + (this.page - 1) * this.commandPerPage;
			if (index < this.commandNames.size()) {

				final String command = this.commandNames.get(index);
				embed.addField(command + " :", this.bot.getHelpFor(command).split("\n")[0]);
			}
		}

		try {

			final Message message = channel.sendMessage(embed).get();
			message.addReaction(EmojiParser.parseToUnicode(":arrow_left:"));
			message.addReaction(EmojiParser.parseToUnicode(":arrow_right:"));
			if (this.bot.commandsIn(Command.Category.UTILITY) != null) message.addReaction(EmojiParser.parseToUnicode(":one:"));
			if (this.bot.commandsIn(Command.Category.FUN) != null) message.addReaction(EmojiParser.parseToUnicode(":two:"));
			if (this.bot.commandsIn(Command.Category.MODERATION) != null) message.addReaction(EmojiParser.parseToUnicode(":three:"));
			if (this.bot.commandsIn(Command.Category.GAME) != null) message.addReaction(EmojiParser.parseToUnicode(":four:"));
			if (this.bot.commandsIn(Command.Category.TUTORIAL) != null) message.addReaction(EmojiParser.parseToUnicode(":five:"));
			final ReactionAddListener listener = event -> {

				final CompletableFuture<User> cfUser1 = event.requestUser();
				final Optional<Reaction> optReaction = event.getReaction();
				if (!optReaction.isPresent()) return;
				final User user1 = cfUser1.join();
				final Reaction reaction = optReaction.get();
				final Emoji emoji = reaction.getEmoji();
				if (!this.bot.is(user1)) {

					App.LOGGER.debug("Reaction '" + EmojiParser.parseToAliases(emoji.getMentionTag()) + "' added !");

					reaction.removeUser(user1);

					switch (EmojiParser.parseToAliases(emoji.getMentionTag())) {

					case ":arrow_right:":
						this.next();
						break;

					case ":arrow_left:":
						this.prev();
						break;

					case ":one:":
						this.changeCategory(Command.Category.UTILITY);
						break;

					case ":two:":
						this.changeCategory(Command.Category.FUN);
						break;

					case ":three:":
						this.changeCategory(Command.Category.MODERATION);
						break;

					case ":four:":
						this.changeCategory(Command.Category.GAME);
						break;

					case ":five:":
						this.changeCategory(Command.Category.TUTORIAL);
						break;

					default:
						return;
					}

					final EmbedBuilder embed1 = this.bot.defaultEmbedBuilder("Aide :", this.category.DESCRIPTION + " :", null);
					embed1.setFooter("Page : " + this.page + " / " + this.numberOfPage, user1.getAvatar());

					for (int i = 0; i < this.commandPerPage; i++) {

						final int index = i + (this.page - 1) * this.commandPerPage;
						if (index < this.commandNames.size()) {

							final String command = this.commandNames.get(index);
							embed1.addField(command + " :", this.bot.getHelpFor(command).split("\n")[0]);
						}
					}
					event.editMessage(embed1);
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
	private void changeCategory(final Command.Category category) {

		if ((this.commandList = this.bot.commandsIn(category)) == null) {

			this.commandList = this.bot.commandsIn(this.category);
			return;
		}

		this.category = category;
		this.commandNames = new ArrayList<>();
		for (final Command command : this.commandList) {

			if (!command.isPrivate()) this.commandNames.add(command.getName());
		}
		Collections.sort(this.commandNames);
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
