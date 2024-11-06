package com.aps;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class TelaCadastroBiometria extends JFrame {
    private JLabel lblCamera;
    private JButton btnCapturar;
    private VideoCapture camera;
    private Mat frame;
    private String emailUsuario;
    private CascadeClassifier faceDetector;

    public TelaCadastroBiometria(String email) {
        this.emailUsuario = email;

        setTitle("Cadastro de Biometria Facial");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        faceDetector = new CascadeClassifier("D:\\Code\\maven\\projeto-aps\\src\\main\\resources\\haarcascade_frontalface_alt.xml");
        iniciarComponentes();
        iniciarCamera();
    }

    private void iniciarComponentes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));

        lblCamera = new JLabel();
        lblCamera.setHorizontalAlignment(JLabel.CENTER);
        lblCamera.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 4));
        panel.add(lblCamera, BorderLayout.CENTER);

        btnCapturar = new JButton("Capturar Biometria");
        btnCapturar.setBackground(new Color(30, 144, 255));
        btnCapturar.setForeground(Color.WHITE);
        btnCapturar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCapturar.setFocusPainted(false);
        btnCapturar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCapturar.addActionListener(e -> capturarImagem());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(panel.getBackground());
        bottomPanel.add(btnCapturar);

        JLabel lblTitulo = new JLabel("Cadastro de Biometria Facial");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void iniciarCamera() {
        camera = new VideoCapture(0);
        frame = new Mat();

        new Thread(() -> {
            while (camera.isOpened()) {
                if (camera.read(frame) && !frame.empty()) {
                    ImageIcon icon = new ImageIcon(convertMatToImage(frame));
                    lblCamera.setIcon(icon);
                } else {
                    System.out.println("Erro ao capturar frame da câmera.");
                    break;
                }
            }
        }).start();
    }

    private void capturarImagem() {
        if (!frame.empty()) {
            Mat rostoDetectado = detectarRosto(frame);

            if (rostoDetectado == null) {
                JOptionPane.showMessageDialog(this, "Nenhum rosto detectado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String caminhoImagemSalva = "imagens/" + emailUsuario + ".png";
            Imgcodecs.imwrite(caminhoImagemSalva, rostoDetectado);

            JOptionPane.showMessageDialog(this, "Cadastro de biometria realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Exibe a tela de login após o cadastro
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
            dispose(); // Fecha a tela de cadastro
        }
    }

    private Mat detectarRosto(Mat imagem) {
        MatOfRect rostosDetectados = new MatOfRect();
        faceDetector.detectMultiScale(imagem, rostosDetectados);

        for (Rect rect : rostosDetectados.toArray()) {
            return new Mat(imagem, rect);
        }
        return null;
    }

    private BufferedImage convertMatToImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    @Override
    public void dispose() {
        camera.release();
        super.dispose();
    }
}
