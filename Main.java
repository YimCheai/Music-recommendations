import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import org.apache.hc.core5.http.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import javazoom.jl.player.Player;

public class Main extends JFrame {
    private SpotifyApi spotifyApi;
    private String genre;
    private String mood;
    private String feeling;
    private String state;
    private String previewUrl; // 추천된 곡의 프리뷰 URL 저장

    public Main() {
        setTitle("Music Recommender");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Spotify API 초기화
        initializeSpotifyApi();

        // 상단 Q1 라벨
        JLabel questionLabel = new JLabel("Q1");
        questionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        questionLabel.setForeground(new Color(255, 182, 193));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 중앙 질문 라벨
        JLabel instructionLabel = new JLabel("선호하는 노래 장르를 선택하세요");
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        // 버튼 생성
        String[] genres = {"팝", "락", "케이팝", "발라드"};
        Color[] colors = {
                new Color(255, 255, 204),
                new Color(255, 229, 204),
                new Color(255, 204, 153),
                new Color(255, 182, 193)
        };

        for (int i = 0; i < genres.length; i++) {
            JButton button = new JButton(genres[i]);
            button.setBackground(colors[i]);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            button.addActionListener(e -> {
                genre = e.getActionCommand();
                nextStep("Q2", "선호하는 노래 분위기를 선택하세요", new String[]{"신나는", "잔잔한", "강렬한", "우울한"});
            });
            buttonPanel.add(button);
        }

        // 레이아웃 구성
        add(questionLabel, BorderLayout.NORTH);
        add(instructionLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void nextStep(String question, String instruction, String[] options) {
        getContentPane().removeAll();

        JLabel questionLabel = new JLabel(question);
        questionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        questionLabel.setForeground(new Color(255, 182, 193));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel instructionLabel = new JLabel(instruction);
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        for (String option : options) {
            JButton button = new JButton(option);
            button.setBackground(new Color(255, 229, 204));
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            button.addActionListener(e -> {
                if (question.equals("Q2")) {
                    mood = option;
                    nextStep("Q3", "현재 기분을 선택하세요", new String[]{"행복함", "슬픔", "평온함", "화남"});
                } else if (question.equals("Q3")) {
                    feeling = option;
                    nextStep("Q4", "현재 상태를 선택하세요", new String[]{"운동 중", "공부 중", "등하굣길", "휴식 중"});
                } else if (question.equals("Q4")) {
                    state = option;
                    showRecommendation();
                }
            });
            buttonPanel.add(button);
        }

        add(questionLabel, BorderLayout.NORTH);
        add(instructionLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void showRecommendation() {
        getContentPane().removeAll();

        String recommendedMusic = recommendMusic();

        JLabel resultLabel = new JLabel("추천 음악");
        resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel musicLabel = new JLabel(recommendedMusic);
        musicLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        musicLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton playButton = new JButton("음악 재생");
        playButton.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        playButton.addActionListener(e -> playPreview());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(musicLabel);
        panel.add(playButton);

        add(resultLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void playPreview() {
        if (previewUrl == null || previewUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "음악을 재생할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Thread(() -> {
            try (BufferedInputStream in = new BufferedInputStream(new URL(previewUrl).openStream())) {
                Player player = new Player(in);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "음악 재생 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
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
                TrackSimplified recommendedTrack = tracks[0];
                previewUrl = recommendedTrack.getPreviewUrl();
                return recommendedTrack.getName() + " by " + recommendedTrack.getArtists()[0].getName();
            } else {
                previewUrl = null;
                return "추천된 음악이 없습니다.";
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            return "추천 중 오류가 발생했습니다.";
        }
    }

    private String mapGenreToSpotifyGenre(String genre) {
        switch (genre) {
            case "팝": return "pop";
            case "락": return "rock";
            case "케이팝": return "k-pop";
            case "발라드": return "ballad";
            default: return "pop";
        }
    }

    private float mapMoodToValence(String mood) {
        switch (mood) {
            case "신나는": return 0.8f;
            case "잔잔한": return 0.4f;
            case "강렬한": return 0.7f;
            case "우울한": return 0.2f;
            default: return 0.5f;
        }
    }

    private float mapStateToEnergy(String state) {
        switch (state) {
            case "운동 중": return 0.9f;
            case "공부 중": return 0.3f;
            case "등하굣길": return 0.6f;
            case "휴식 중": return 0.4f;
            default: return 0.5f;
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
