package com.example.playvideo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Constants {

    public static final String rosString = "std_msgs/String";
    public static final String rosFloat = "std_msgs/Float32";
    public static final String rosInteger = "std_msgs/Int8";
    public static final String rosBoolean = "std_msgs/Bool";

    // ROAMIO_DEV_1
    public static final String mc_token = "T09b152f802f2e8525a212822";
    public static final String mc_secret = "Sc69a1ec14ef5b79f194fcdfe";
    public static final String device_id = "D9278AEA9BB2C42E8E5C11FFF66";
    public static final String account_id = "AF9111FB06CD795D636B8E2AA";

    // KORECHI RESETAPI
    public static final String mc_token_test = "Tb5d72897de4fe84b0eb56271";
    public static final String mc_secret_test = "S1786442d9cfae81783c0745b";
    public static final String device_id_test = "D40A19737B5CFE9122E55BE5AC6";
    public static final String account_id_test = "AF9111FB06CD795D636B8E2AA";

    // ROAMIO_HCT
    public static final String mc_token_hct = "T34013d06cb0d5fa357f341bc";
    public static final String mc_secret_hct = "Sefe8b04c8db5a3e13f0b4627";
    public static final String device_id_hct = "DE3BCB1B2BF7D886A785E1F8276";
    public static final String account_id_hct = "AF9111FB06CD795D636B8E2AA";


    public static final String Post_Command = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/commands?mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct;

    public static final String getRoamIOLocation = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/data?mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&topics=[\"/gps/ublox_gps/fix\",\"/RoamIO/heading\"]&utc_start=-1s";

    public static final String getNavigationStatus = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/data?platforms=[\"ros\"]&mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&topics=[\"/RoamIO/Navigation/InProgress\"]&utc_start=-1s";

    public static final String getRoamIOMessages = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/data?platforms=%5B%22ros%22%5D&mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&topics=%5B%22%2FRoamIO%2Fmessages%22%5D&utc_start=-1s";

    public static final String getLEDStatus = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/data?mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&utc_start=-1s&topics=%5B%22%2FLidarWorking%22%2C%22%2FRoamIO%2Frtk_fix%2Fapp_ver%22%2C%22%2FRoamIO%2FSpreader%2Fstatus%22%5D";

    public static final String getLEDStatus1 = "https://api.freedomrobotics.ai/accounts/"+account_id_hct+"/devices/"+device_id_hct+"/data?mc_token="+mc_token_hct+"&mc_secret="+mc_secret_hct+"&utc_end=now&utc_start=-1s&topics=[\"/LidarWorking\",\"/RoamIO/rtk_fix/app_ver\",\"/RoamIO/Spreader/status\"]";

    public static final String getNextCoordinate = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/data?platforms=[\"ros\"]&mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&topics=[\"/RoamIO/nextDestination\"]&utc_start=-1s";

    public static final String getRoamioInformation = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/data?mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&utc_start=-1s&topics=[\"/batteryGeneral\",\"/RoamIO/speed/app_ver\",\"/RAPG/totalArea\",\"/RAPG/totalDistance\",\"/RoamIO/navigation/presentAreaCovered\",\"/RoamIO/navigation/presentDistanceCovered\",\"/RoamIO/navigation/presentRunTime\",\"/RoamIO/AverageSpeed/app_ver\",\"/RoamIO/runTime\"]";

    public static final String getImage = "https://api.freedomrobotics.ai/accounts/" + account_id_hct + "/devices/" + device_id_hct + "/videos?mc_token=" + mc_token_hct + "&mc_secret=" + mc_secret_hct + "&utc_end=now&pre_signed=true&utc_start=-1s";

    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("api.freedomrobotics.ai");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.SECONDS);
            future.cancel(true);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        return inetAddress != null && !inetAddress.equals("");
    }


}
