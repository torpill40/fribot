package com.torpill.fribot.threads;

import java.awt.Color;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.MessageCreateListener;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.util.ListBuilder;

/**
 *
 * Cette classe représente un thread pour jouer au Juste Prix.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.threads.BotThread
 *
 */

public class RightPriceThread extends BotThread {

	private int attempt = 0;
	private boolean find = false;
	private boolean end = false;
	private long time = 0;

	/**
	 *
	 * Constructeur de la classe <code>RightPriceThread</code>.
	 *
	 * @param bot
	 *            : bot Discord relié au thread.
	 *
	 * @see com.torpill.fribot.bot.DiscordBot
	 */
	public RightPriceThread(final DiscordBot bot) {

		super(bot, "Right Price");
	}

	@Override
	protected List<? extends Class<?>> args() {

		return ListBuilder.listOf(User.class, TextChannel.class, Integer.class, Integer.class, Integer.class);
	}

	@Override
	public void run() {

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' démarré avec succès.");

		final User user = (User) this.args[0];
		final TextChannel channel = (TextChannel) this.args[1];
		final int number = (int) this.args[2];
		final int min = (int) this.args[3];
		final int max = (int) this.args[4];

		App.LOGGER.debug("'" + Thread.currentThread().getName() + "' paramètres récupérés avec succès.");

		channel.sendMessage(this.bot.defaultEmbedBuilder("Juste Prix", "Le nombre à trouver se situe entre " + min + " et " + max + ".", user));
		final MessageCreateListener listener = event -> {

			event.getMessageAuthor().asUser().ifPresent(user0 -> {

				if (user0.getId() == user.getId() && !this.end) {

					final String msg = event.getMessage().getContent();

					try {

						final int n = Integer.parseInt(msg);
						this.time = System.currentTimeMillis();
						this.attempt++;
						this.find = n == number;
						this.end = this.attempt == 15 || this.find;
						final EmbedBuilder embed = this.bot.defaultEmbedBuilder("Juste Prix", null, null);
						embed.setFooter("Essai n°" + this.attempt, user.getAvatar());
						if (this.find) embed.setDescription("Bravo ! Tu as trouvé le nombre mystère " + number + " !").setColor(new Color(0x19BE19));
						else if (!this.end) embed.setDescription("C'est " + (n < number ? "plus" : "moins") + " !");
						else embed.setDescription("Dommage... Tu n'as pas réussi à trouver le nombre mystère " + number + " en moins de 15 essais...").setColor(new Color(0xEA3323));
						channel.sendMessage(embed);

					} catch (final NumberFormatException e) {

						if (msg.equalsIgnoreCase("cancel")) {

							this.end = true;
							channel.sendMessage(this.bot.defaultEmbedBuilder("Juste Prix", "Tu as interrompue ta partie. Recommences une nouvelle partie quand tu veux !", user));
						}
					}
				}
			});
		};
		this.time = System.currentTimeMillis();
		channel.addMessageCreateListener(listener);
		try {

			while (!this.end) {

				Thread.sleep(2000);
				if (System.currentTimeMillis() > this.time + 30000) {

					this.end = true;
					channel.sendMessage(this.bot.defaultEmbedBuilder("Juste Prix", "Ta partie a été interrompue car tu as mis trop de temps pour proposer un nouveau nombre. Recommences une nouvelle partie quand tu veux !", user));
				}
			}

		} catch (final InterruptedException e) {

			e.printStackTrace();
			this.end = true;
		}
		channel.removeListener(MessageCreateListener.class, listener);
	}
}
