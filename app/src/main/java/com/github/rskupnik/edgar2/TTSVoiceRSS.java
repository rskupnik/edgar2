package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.tts.TextToSpeechAdapter;
import com.voicerss.tts.*;

import java.io.FileOutputStream;

public class TTSVoiceRSS implements TextToSpeechAdapter {

    private final String apiKey;
    private final VoiceProvider voiceProvider;

    public TTSVoiceRSS(String apiKey) {
        this.apiKey = apiKey;
        this.voiceProvider = new VoiceProvider(apiKey);
    }

    @Override
    public void speak(String text) {
        VoiceParameters params = new VoiceParameters(text, Languages.English_GreatBritain);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_8KHZ.AF_8khz_8bit_mono);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
        params.setVoice("Lily");

        try {
            byte[] voice = voiceProvider.speech(params);

            FileOutputStream fos = new FileOutputStream("voice.wav");
            fos.write(voice, 0, voice.length);
            fos.flush();
            fos.close();

            Runtime.getRuntime().exec("paplay voice.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
