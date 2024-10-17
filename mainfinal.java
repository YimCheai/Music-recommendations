package schoolJavaProj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainfinal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MusicRecommenderGUI());
    }
}

class MusicRecommenderGUI extends JFrame {
    private String genre;
    private String mood;
    private String feeling;
    private String state;

    public MusicRecommenderGUI() {
        setTitle("Music Recommender");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        // 장르 선택
        genre = showOptions("선호하는 노래 장르를 선택하세요", new String[]{"팝", "락", "케이팝", "발라드"});
        if (genre == null) return; 

        // 분위기 선택
        mood = showOptions("선호하는 노래 분위기를 선택하세요", new String[]{"신나는", "잔잔한", "강렬한", "우울한"});
        if (mood == null) return;

        // 기분 선택
        feeling = showOptions("현재 기분을 선택하세요", new String[]{"행복함", "슬픔", "평온함", "화남"});
        if (feeling == null) return;

        // 상태 선택
        state = showOptions("현재 상태를 선택하세요", new String[]{"운동 중", "공부 중", "등하굣길", "휴식 중"});
        if (state == null) return;

        // 추천 버튼
        JButton recommendButton = new JButton("음악 추천");
        add(recommendButton);
        recommendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recommendedMusic = recommendMusic();
                JOptionPane.showMessageDialog(MusicRecommenderGUI.this, "추천 음악: " + recommendedMusic);
            }
        });

        setVisible(true);
    }

    private String showOptions(String title, String[] options) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(options.length, 1));
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton[] radioButtons = new JRadioButton[options.length];

        for (int i = 0; i < options.length; i++) {
            radioButtons[i] = new JRadioButton(options[i]);
            panel.add(radioButtons[i]);
            buttonGroup.add(radioButtons[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            for (JRadioButton button : radioButtons) {
                if (button.isSelected()) {
                    return button.getText();
                }
            }
        }
        return null; 
    }

    private String recommendMusic() {
       
        if (genre == null || mood == null || feeling == null || state == null) {
            return "모든 옵션을 선택해야 음악을 추천할 수 있습니다!";
        }

        if (genre.equals("팝")) {
            if (mood.equals("신나는")) {
                if (feeling.equals("행복함") && state.equals("휴식 중")) {
                    return "Happy by Pharrell Williams";
                } else if (feeling.equals("화남")) {
                    return "Uptown Funk by Mark Ronson ft. Bruno Mars";
                } else if (state.equals("운동 중")) {
                    return "Can't Stop the Feeling! by Justin Timberlake";
                }
            } else if (mood.equals("잔잔한")) {
                return "Someone Like You by Adele";
            } else if (mood.equals("강렬한")) {
                return "Bad Guy by Billie Eilish";
            } else if (mood.equals("우울한")) {
                return "When We Were Young by Adele";
            }
        } else if (genre.equals("락")) {
            if (mood.equals("신나는")) {
                return "Don't Stop Me Now by Queen";
            } else if (mood.equals("강렬한") && feeling.equals("화남")) {
                return "Smells Like Teen Spirit by Nirvana";
            } else if (mood.equals("잔잔한") && feeling.equals("슬픔")) {
                return "Nothing Else Matters by Metallica";
            } else if (mood.equals("우울한")) {
                return "Creep by Radiohead";
            }
        } else if (genre.equals("케이팝")) {
            if (mood.equals("신나는")) {
                return "Dynamite by BTS";
            } else if (mood.equals("잔잔한")) {
                return "Spring Day by BTS";
            } else if (mood.equals("강렬한")) {
                return "God's Menu by Stray Kids";
            } else if (mood.equals("우울한") && feeling.equals("슬픔")) {
                return "Blue & Grey by BTS";
            }
        } else if (genre.equals("발라드")) {
            if (mood.equals("신나는") && state.equals("등하굣길")) {
                return "나의 사춘기에게 by 볼빨간사춘기";
            } else if (mood.equals("잔잔한") && feeling.equals("평온함")) {
                return "바람이 분다 by 이소라";
            } else if (mood.equals("우울한") && feeling.equals("슬픔")) {
                return "사랑했지만 by 김광석";
            } else if (mood.equals("강렬한")) {
                return "내 손을 잡아 by 아이유";
            }
        }

        //추천 음악
        return "당신에게 맞는 곡을 찾고 있습니다!";
    }
}
