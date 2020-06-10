package com.torpill.fribot.bot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.javacord.api.DiscordApiBuilder;

import com.torpill.fribot.commands.Command;
import com.torpill.fribot.commands.fun.ImageCommand;
import com.torpill.fribot.commands.utility.DevRoleCommand;
import com.torpill.fribot.commands.utility.HelpCommand;
import com.torpill.fribot.commands.utility.InfoCommand;
import com.torpill.fribot.commands.utility.KeyArgsCommand;
import com.torpill.fribot.commands.utility.NoneArgsCommand;
import com.torpill.fribot.commands.utility.QuoteArgsCommand;
import com.torpill.fribot.commands.utility.RawArgsCommand;
import com.torpill.fribot.commands.utility.TypeCommand;
import com.torpill.fribot.listeners.BotListener;
import com.torpill.fribot.listeners.CommandListener;
import com.torpill.fribot.threads.BotThread;
import com.torpill.fribot.threads.CommandThread;
import com.torpill.fribot.threads.HelpThread;

/**
 *
 * Cette classe représente un constructeur de bot Discord.
 *
 * @author torpill40
 *
 */

public class DiscordBotBuilder {

	private String token;
	private String prefix = "?:";
	private Color color = Color.WHITE;
	private String role = null, devrole = null;
	private final List<Class<? extends BotListener>> listeners;
	private final List<Class<? extends Command>> commands;
	private final List<Class<? extends BotThread>> threads;

	/**
	 *
	 * Constructeur de la classe <code>DiscordBotBuilder</code>.
	 *
	 */
	public DiscordBotBuilder() {

		this.listeners = new ArrayList<>();
		this.commands = new ArrayList<>();
		this.threads = new ArrayList<>();
	}

	/**
	 *
	 * Construire le bot Discord.
	 *
	 * @return bot discord
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 *
	 * @see org.javacord.api.DiscordApiBuilder
	 * @see org.javacord.api.DiscordApi
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public DiscordBot build() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		final DiscordApiBuilder builder = new DiscordApiBuilder().setToken(this.token);
		final DiscordBot bot = new DiscordBot(this.prefix, this.color, this.role, this.devrole);

		// @formatter:off

		this.addListener(CommandListener.class)
			.addCommand(HelpCommand.class)
			.addCommand(InfoCommand.class)
			.addCommand(NoneArgsCommand.class)
			.addCommand(RawArgsCommand.class)
			.addCommand(QuoteArgsCommand.class)
			.addCommand(KeyArgsCommand.class)
			.addCommand(TypeCommand.class)
			.addCommand(DevRoleCommand.class)
			.addCommand(ImageCommand.class)
			.addThread(CommandThread.class)
			.addThread(HelpThread.class);

		// @formatter:on

		for (final Class<? extends BotListener> listener : this.listeners) {

			builder.addListener(listener.getConstructor(DiscordBot.class).newInstance(bot));
		}

		for (final Class<? extends Command> command : this.commands) {

			bot.addCommand(command.newInstance());
		}

		for (final Class<? extends BotThread> thread : this.threads) {

			bot.addThread(thread.getConstructor(DiscordBot.class).newInstance(bot));
		}

		return bot.api(builder.login().join());
	}

	/**
	 *
	 * Configurer le token du bot.
	 *
	 * @param token
	 *            : token du bot.
	 * @return this
	 */
	public DiscordBotBuilder setToken(final String token) {

		this.token = token;
		return this;
	}

	/**
	 *
	 * Configurer le préfix du bot.
	 *
	 * @param prefix
	 *            : préfix du bot.
	 * @return this
	 */
	public DiscordBotBuilder setPrefix(final String prefix) {

		this.prefix = prefix;
		return this;
	}

	/**
	 *
	 * Configurer la couleur par défaut du bot.
	 *
	 * @param color
	 *            : couleur par défaut du bot.
	 * @return this
	 */
	public DiscordBotBuilder setColor(final Color color) {

		this.color = color;
		return this;
	}

	/**
	 *
	 * Configurer le rôle utilisateur du bot.
	 *
	 * @param role
	 *            : ID du rôle utilisateur.
	 * @return this
	 */
	public DiscordBotBuilder setRole(final String role) {

		this.role = role;
		return this;
	}

	/**
	 *
	 * Configurer le rôle développeur du bot.
	 *
	 * @param role
	 *            : ID du rôle utilisateur.
	 * @return this
	 */
	public DiscordBotBuilder setDevRole(final String devrole) {

		this.devrole = devrole;
		return this;
	}

	/**
	 *
	 * Ajouter un écouteur au bot.<br />
	 * Pour fonctionner correctement, la classe héritée de <code>BotListener</code>
	 * doit avoir au minimum un contructeur n'ayant comme paramètres qu'un seul
	 * paramètre de type <code>DiscordBot</code>.
	 *
	 * @param listener
	 *            : classe de l'écouteur à ajouter.
	 * @return this
	 *
	 * @see com.torpill.fribot.listeners.BotListener
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public DiscordBotBuilder addListener(final Class<? extends BotListener> listener) {

		this.listeners.add(listener);
		return this;
	}

	/**
	 *
	 * Ajouter une commande au bot.<br />
	 * Pour fonctionner correctement, la classe héritée de <code>Command</code> doit
	 * avoir au minimum un contructeur n'ayant aucun paramètres requis.
	 *
	 * @param command
	 *            : classe de la commande à ajouter.
	 * @return this
	 *
	 * @see com.torpill.fribot.commands.Command
	 */
	public DiscordBotBuilder addCommand(final Class<? extends Command> command) {

		this.commands.add(command);
		return this;
	}

	/**
	 *
	 * Ajouter un thread au bot.<br />
	 * Pour fonctionner correctement, la classe héritée de <code>BotThread</code>
	 * doit avoir au minimum un contructeur n'ayant comme paramètres qu'un seul
	 * paramètre de type <code>DiscordBot</code>.
	 *
	 * @param thread
	 *            : classe du thread à ajouter.
	 * @return this
	 *
	 * @see com.torpill.fribot.threads.BotThread
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public DiscordBotBuilder addThread(final Class<? extends BotThread> thread) {

		this.threads.add(thread);
		return this;
	}
}
