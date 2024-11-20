package org.example;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import org.apache.hc.core5.http.ParseException;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MusicRecommenderGUI());
    }
}

class MusicRecommenderGUI extends JFrame {
    private String genre;
    private String mood;
    private String feeling;
    private String state;
    private SpotifyApi spotifyApi;

    public MusicRecommenderGUI() {
        setTitle("Music Recommender");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeSpotifyApi();

        // 진행 상태 패널
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(255, 245, 247));
        headerPanel.setLayout(new BorderLayout());
        JLabel stepLabel = new JLabel("Step 1: Select Genre", SwingConstants.CENTER);
        stepLabel.setFont(new Font("Arial", Font.BOLD, 18));
        stepLabel.setForeground(new Color(255, 102, 102));
        headerPanel.add(stepLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // 옵션 선택
        genre = showOptions("선호하는 노래 장르를 선택하세요", new String[]{"팝", "락", "케이팝", "발라드"});
        if (genre == null) return;

        stepLabel.setText("Step 2: Select Mood");
        mood = showOptions("선호하는 노래 분위기를 선택하세요", new String[]{"신나는", "잔잔한", "강렬한", "우울한"});
        if (mood == null) return;

        stepLabel.setText("Step 3: Select Feeling");
        feeling = showOptions("현재 기분을 선택하세요", new String[]{"행복함", "슬픔", "평온함", "화남"});
        if (feeling == null) return;

        stepLabel.setText("Step 4: Select State");
        state = showOptions("현재 상태를 선택하세요", new String[]{"운동 중", "공부 중", "등하굣길", "휴식 중"});
        if (state == null) return;

        // 추천 버튼
        JButton recommendButton = new JButton("추천 음악 보기");
        recommendButton.setBackground(new Color(255, 102, 102));
        recommendButton.setForeground(Color.WHITE);
        recommendButton.setFont(new Font("Arial", Font.BOLD, 16));
        recommendButton.setFocusPainted(false);
        recommendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(recommendButton);

        add(buttonPanel, BorderLayout.SOUTH);

        recommendButton.addActionListener(e -> {
            String recommendedMusic = recommendMusic();
            JOptionPane.showMessageDialog(this, "추천 음악: " + recommendedMusic);
        });

        setVisible(true);
    }

    private void initializeSpotifyApi() {
        String clientId = "c3928a4b3c1b410b89aa58db37dface2";
        String clientSecret = "f42f7d0af7f34e22b142414593852fbd";

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        try {
            String accessToken = spotifyApi.clientCredentials().build().execute().getAccessToken();
            spotifyApi.setAccessToken(accessToken);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    private String recommendMusic() {
        if (genre == null || mood == null || feeling == null || state == null) {
            return "모든 옵션을 선택해야 음악을 추천할 수 있습니다!";
        }

        try {
            GetRecommendationsRequest recommendationsRequest = spotifyApi.getRecommendations()
                    .seed_genres(mapGenreToSpotifyGenre(genre))
                    .target_valence(mapMoodToValence(mood))
                    .target_energy(mapStateToEnergy(state))
                    .limit(1)
                    .build();

            Recommendations recommendations = recommendationsRequest.execute();
            TrackSimplified[] tracks = recommendations.getTracks();

            if (tracks.length > 0) {
                Track recommendedTrack = tracks[0];
                String previewUrl = recommendedTrack.getPreviewUrl();

                if (previewUrl != null) {
                    playPreview(previewUrl);
                    return recommendedTrack.getName() + " by " + recommendedTrack.getArtists()[0].getName();
                } else {
                    return "추천된 음악은 있지만 미리 듣기를 제공하지 않습니다: " +
                            recommendedTrack.getName() + " by " + recommendedTrack.getArtists()[0].getName();
                }
            } else {
                return "조건에 맞는 음악을 찾을 수 없습니다.";
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            return "음악 추천 중 오류가 발생했습니다.";
        }
    }

    private void playPreview(String previewUrl) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new URL(previewUrl));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(30000);
            clip.stop();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오디오 재생 중 오류가 발생했습니다.");
        }
    }

    private String mapGenreToSpotifyGenre(String genre) {
        switch (genre) {
            case "팝":
                return "pop";
            case "락":
                return "rock";
            case "케이팝":
                return "k-pop";
            case "발라드":
                return "ballad";
            default:
                return "pop";
        }
    }

    private float mapMoodToValence(String mood) {
        switch (mood) {
            case "신나는":
                return 0.8f;
            case "잔잔한":
                return 0.4f;
            case "강렬한":
                return 0.7f;
            case "우울한":
                return 0.2f;
            default:
                return 0.5f;
        }
    }

    private float mapStateToEnergy(String state) {
        switch (state) {
            case "운동 중":
                return 0.9f;
            case "공부 중":
                return 0.3f;
            case "등하굣길":
                return 0.6f;
            case "휴식 중":
                return 0.2f;
            default:
                return 0.5f;
        }
    }

    private String showOptions(String title, String[] options) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ButtonGroup buttonGroup = new ButtonGroup();

        for (String option : options) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
            buttonGroup.add(radioButton);
            panel.add(radioButton);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            for (Component component : panel.getComponents()) {
                if (component instanceof JRadioButton) {
                    JRadioButton radioButton = (JRadioButton) component;
                    if (radioButton.isSelected()) {
                        return radioButton.getText();
                    }
                }
            }
        }
        return null;
    }
}


