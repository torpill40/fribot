package com.torpill.fribot.bot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.javacord.api.DiscordApiBuilder;

import com.torpill.fribot.commands.Command;
import com.torpill.fribot.commands.HelpCommand;
import com.torpill.fribot.commands.InfoCommand;
import com.torpill.fribot.commands.KeyArgsCommand;
import com.torpill.fribot.commands.NoneArgsCommand;
import com.torpill.fribot.commands.QuoteArgsCommand;
import com.torpill.fribot.commands.RawArgsCommand;
import com.torpill.fribot.commands.TypeCommand;
import com.torpill.fribot.listeners.BotListener;
import com.torpill.fribot.listeners.CommandListener;
import com.torpill.fribot.threads.BotThread;
import com.torpill.fribot.threads.CommandThread;
import com.torpill.fribot.threads.HelpThread;

public class DiscordBotBuilder {

	private String token;
	private String prefix = "?:";
	private Color color = Color.WHITE;
	private String role = null;
	private final List<Class<? extends BotListener>> listeners;
	private final List<Class<? extends Command>> commands;
	private final List<Class<? extends BotThread>> threads;

	public DiscordBotBuilder() {

		this.listeners = new ArrayList<>();
		this.commands = new ArrayList<>();
		this.threads = new ArrayList<>();
	}

	public DiscordBot build() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		final DiscordApiBuilder builder = new DiscordApiBuilder().setToken(this.token);
		final DiscordBot bot = new DiscordBot(this.prefix, this.color, this.role);

		// @formatter:off
		
		this.addListener(CommandListener.class)
			.addCommand(HelpCommand.class)
			.addCommand(InfoCommand.class)
			.addCommand(NoneArgsCommand.class)
			.addCommand(RawArgsCommand.class)
			.addCommand(QuoteArgsCommand.class)
			.addCommand(KeyArgsCommand.class)
			.addCommand(TypeCommand.class)
			.addThread(CommandThread.class)
			.addThread(HelpThread.class);
		
		// @formatter:on

		for (Class<? extends BotListener> listener : this.listeners) {

			builder.addListener(listener.getConstructor(DiscordBot.class).newInstance(bot));
		}

		for (Class<? extends Command> command : this.commands) {

			bot.addCommand(command.newInstance());
		}

		for (Class<? extends BotThread> thread : this.threads) {

			bot.addThread(thread.getConstructor(DiscordBot.class).newInstance(bot));
		}

		return bot.api(builder.login().join());
	}

	public DiscordBotBuilder setToken(String token) {

		this.token = token;
		return this;
	}

	public DiscordBotBuilder setPrefix(String prefix) {

		this.prefix = prefix;
		return this;
	}

	public DiscordBotBuilder setColor(Color color) {

		this.color = color;
		return this;
	}

	public DiscordBotBuilder setRole(String role) {

		this.role = role;
		return this;
	}

	public DiscordBotBuilder addListener(final Class<? extends BotListener> listener) {

		this.listeners.add(listener);
		return this;
	}

	public DiscordBotBuilder addCommand(final Class<? extends Command> command) {

		this.commands.add(command);
		return this;
	}

	public DiscordBotBuilder addThread(final Class<? extends BotThread> thread) {

		this.threads.add(thread);
		return this;
	}
}
