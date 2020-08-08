package com.torpill.fribot.commands.fun;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import com.torpill.fribot.App;
import com.torpill.fribot.bot.DiscordBot;
import com.torpill.fribot.commands.Command;
import com.torpill.fribot.util.ImageProcessor;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

/**
 *
 * Dire au revoir à quelqu'un avec une courte vidéo.
 *
 * @author torpill40
 *
 * @see com.torpill.fribot.commands.Command
 *
 */
public class ByeBroCommand extends Command {

	/**
	 *
	 * Constructeur de la classe <code>ByeBroCommand</code>.
	 *
	 */
	public ByeBroCommand() {

		super("bye-bro", Command.ArgumentType.RAW, Command.Category.FUN);
	}

	@Override
	public String getHelp() {

		return "Dire au revoir à quelqu'un avec une courte vidéo.";
	}

	@Override
	public String getExample(final String prefix, final User user) {

		return Command.defaultExampleForOneMemberArgument(this, prefix, user);
	}

	@Override
	public boolean deleteCommandUsage() {

		return true;
	}

	@Override
	public List<Role> whiteListedRoles(final DiscordBot bot, final Server server) {

		return bot.roles(server, "user-role");
	}

	@Override
	public int execute(final DiscordBot bot, final String[] args, final User user, final TextChannel channel, final Server server) {

		if (args.length == 0) return 1;

		final User other = bot.getUser(server, channel, user, args[0]);
		if (other == null) return 2;
		if (other.getId() == user.getId()) {

			channel.sendMessage(user.getMentionTag() + ", tu ne peux pas te dire au revoir à toi même !");
			return 2;
		}

		final Random rand = new Random();
		final long tempId = App.TEMP.createInstance();
		App.TEMP.addFile(tempId, "pp1", Long.toHexString(user.getId()) + "-" + (rand.nextInt(8_999_999) + 1_000_000) + ".png");
		App.TEMP.addFile(tempId, "pp2", Long.toHexString(other.getId()) + "-" + (rand.nextInt(8_999_999) + 1_000_000) + ".png");
		App.TEMP.addFile(tempId, "vid", Long.toHexString(user.getId()) + "-" + Long.toHexString(other.getId()) + ".mp4");
		try {

			ImageIO.write(ImageProcessor.makeRoundedCorner(ImageProcessor.resize(bot.getAvatar(user), 210), 50), "PNG", App.TEMP.getTempFile(tempId, "pp1"));
			ImageIO.write(ImageProcessor.makeRoundedCorner(ImageProcessor.resize(bot.getAvatar(other), 190), 50), "PNG", App.TEMP.getTempFile(tempId, "pp2"));

			final FFmpeg ffmpeg = new FFmpeg(System.getenv("FFMPEG_HOME") + "/bin/ffmpeg");
			final FFprobe ffprobe = new FFprobe(System.getenv("FFMPEG_HOME") + "/bin/ffprobe");

			//@formatter:off

			new FFmpegExecutor(ffmpeg, ffprobe).createJob(new FFmpegBuilder()
				.setInput(App.SRC + "/external/videos/salut-mon-pote.mp4")
				.overrideOutputFiles(true)
				.addInput(App.TEMP.getTempFilePath(tempId, "pp1"))
				.addInput(App.TEMP.getTempFilePath(tempId, "pp2"))
				.setComplexFilter("[0:v][1:v] overlay=enable='between(t,0,2.5)':x=280:y=40[bg0]; "
								+ "[bg0][1:v] overlay=enable='between(t,2.5,4.78479)':x=280+sin((2.5-t)*2.75)*70:y=40[bg1]; "
								+ "[bg1][1:v] overlay=enable='between(t,4.78479,9.35)':x=280:y=40[bg2]; "
								+ "[bg2][2:v] overlay=enable='between(t,9.35,12)':x=200:y=25")
				.addOutput(App.TEMP.getTempFilePath(tempId, "vid"))
				.done(), progress -> {

					if (progress.isEnd()) channel.sendMessage(App.TEMP.getTempFile(tempId, "vid")).thenRun(() -> App.TEMP.deleteAll(tempId));
				})
			.run();

			//@formatter:on

		} catch (final IOException | RuntimeException e) {

			App.LOGGER.error("ERREUR: ", e);
			channel.sendMessage("```Une erreur est survenue : " + e.getMessage() + "```");
			App.TEMP.deleteAll(tempId);
			return 2;
		}

		return 0;
	}
}
