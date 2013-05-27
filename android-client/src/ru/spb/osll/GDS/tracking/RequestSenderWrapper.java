/**
 * 
 */
package ru.spb.osll.GDS.tracking;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.spb.osll.GDS.GDSUtil;
import ru.spb.osll.json.Errno;
import ru.spb.osll.json.JsonAddUserRequest;
import ru.spb.osll.json.JsonAddUserResponse;
import ru.spb.osll.json.JsonApplyChannelRequest;
import ru.spb.osll.json.JsonApplyChannelResponse;
import ru.spb.osll.json.JsonApplyMarkRequest;
import ru.spb.osll.json.JsonApplyMarkResponse;
import ru.spb.osll.json.JsonFilterCircleRequest;
import ru.spb.osll.json.JsonFilterResponse;
import ru.spb.osll.json.JsonLoginRequest;
import ru.spb.osll.json.JsonLoginResponse;
import ru.spb.osll.json.JsonSubscribeRequest;
import ru.spb.osll.json.JsonSubscribeResponse;
import ru.spb.osll.json.RequestException;
import ru.spb.osll.json.SafeRequestSender;
import ru.spb.osll.objects.Channel;

/**
 * Common routines for requests sending 
 * @author Mark Zaslavskiy
 *
 */
public class RequestSenderWrapper {

	private static final int ATTEMPTS = 3;
	private static final String GENERATED_CHANNEL_DESCRIPTION = "";
	private static final String GENERATED_CHANNEL_URL = "";
	private static final String GENERATED_TAG_DESCRIPTION = "";
	private static final String GENERATED_TAG_TITLE = "";
	private static final String GENERATED_TAG_LINK = "";
	
	public static String login(String login, String password, String serverUrl) throws RequestException{
		
		JsonLoginRequest req = new JsonLoginRequest(login, password, serverUrl);
		JsonLoginResponse res = new JsonLoginResponse();
		
		int[] possibleErrnos = {Errno.SUCCESS};
		
		SafeRequestSender.safeSendingRequest(req, res, "LoginResponse is empty!",
				"Login error = ", ATTEMPTS, possibleErrnos);
		
		return res.getAuthString();
	}
	
	public static void addChannel(String authToken, String channelName, String serverUrl)throws RequestException{

		JsonApplyChannelRequest req = new JsonApplyChannelRequest(authToken, channelName, GENERATED_CHANNEL_DESCRIPTION, GENERATED_CHANNEL_URL, 0, serverUrl);
		JsonApplyChannelResponse res = new JsonApplyChannelResponse();
		
		int[] possibleErrnos = {Errno.SUCCESS, Errno.CHANNEL_ALREADY_EXIST_ERROR};
		
		SafeRequestSender.safeSendingRequest(req, res, "AddChannel response is empty!",
				"ApplyChannel error = ", ATTEMPTS, possibleErrnos);
	}
	
	public static void addUser(String login, String password, String email, String serverUrl)throws RequestException{
		JsonAddUserRequest req = new JsonAddUserRequest(email, login, password,serverUrl);
		JsonAddUserResponse res = new JsonAddUserResponse();
		
		int[] possibleErrnos = {Errno.SUCCESS};
		
		SafeRequestSender.safeSendingRequest(req, res, "AddUser response is empty!",
				"AddUser error =  ", ATTEMPTS, possibleErrnos);
	}
	
	public static void subscribeChannel(String authToken, String channelName, String serverUrl)throws RequestException{
		JsonSubscribeRequest req = new JsonSubscribeRequest(authToken, channelName, serverUrl);
		JsonSubscribeResponse res = new JsonSubscribeResponse();
		
		int[] possibleErrnos = {Errno.SUCCESS, Errno.CHANNEL_ALREADY_SUBSCRIBED_ERROR};
		
		SafeRequestSender.safeSendingRequest(req, res, "SubscribeChannel response is empty!",
				"SubscribeChannel error = ", ATTEMPTS, possibleErrnos);
	}
	
	public static void writeTag(String authToken, String channelName, double latitude, double longitude, String serverUrl)throws RequestException{
		String time = GDSUtil.getUtcTime(new Date());
		
		JsonApplyMarkRequest req = new JsonApplyMarkRequest(authToken, channelName, GENERATED_TAG_TITLE, GENERATED_TAG_LINK,
				GENERATED_TAG_DESCRIPTION, latitude, longitude, 0, time, serverUrl);
		JsonApplyMarkResponse res = new JsonApplyMarkResponse();
		
		int[] possibleErrnos = {Errno.SUCCESS};
		
		SafeRequestSender.safeSendingRequest(req, res, "WriteTag response is empty!",
				"WriteTag error =  ", ATTEMPTS, possibleErrnos);
	}
	
	public static List<Channel> filterCircleRequest(String authToken, double latitude, double longitude,
			double radius, String serverUrl) throws RequestException{

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, 1); // just in case
		String timeTo = GDSUtil.getUtcTime(calendar.getTime());
		calendar.add(Calendar.YEAR, -1); // return current date
		calendar.add(Calendar.HOUR, - GDSUtil.RELEVANT_PERIOD_IN_HOURS);
		String timeFrom = GDSUtil.getUtcTime(calendar.getTime());
		
		JsonFilterCircleRequest req = new JsonFilterCircleRequest(authToken,
				latitude, longitude, radius, timeFrom, timeTo, serverUrl);

		JsonFilterResponse res = new JsonFilterResponse();
		
		int[] possibleErrnos = {Errno.SUCCESS};
		
		SafeRequestSender.safeSendingRequest(req, res, "FilterCircle response is empty!",
				"FilterCircle error =  ", ATTEMPTS, possibleErrnos);
		
		return res.getChannelsData();
	}
}
