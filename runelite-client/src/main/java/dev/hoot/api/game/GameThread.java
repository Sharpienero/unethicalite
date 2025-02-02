package dev.hoot.api.game;

import dev.hoot.bot.managers.Static;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class GameThread
{
	private static final long TIMEOUT = 1000;

	public static void invoke(Runnable runnable)
	{
		if (Static.getClient().isClientThread())
		{
			runnable.run();
		}
		else
		{
			Static.getClientThread().invokeLater(runnable);
		}
	}

	public static <T> T invokeLater(Callable<T> callable)
	{
		if (Static.getClient().isClientThread())
		{
			try
			{
				return callable.call();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			FutureTask<T> futureTask = new FutureTask<>(callable);
			Static.getClientThread().invokeLater(futureTask);
			return futureTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
		}
		catch (ExecutionException | InterruptedException | TimeoutException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Client thread invoke timed out after " + TIMEOUT + " ms");
		}
	}
}

