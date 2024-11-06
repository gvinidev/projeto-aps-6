package com.aps;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.MatOfRect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class TelaAutenticacaoBiometria extends JFrame {

    static {
        // Carrega a biblioteca OpenCV
        System.load("D:\\Code\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    private JLabel lblCamera;
    private JButton btnAutenticar;
    private VideoCapture camera;
    private Mat frame;
    private String emailUsuario;
    private CascadeClassifier faceDetector;

    public TelaAutenticacaoBiometria(String email) {
        // Configura a janela de autenticação e inicializa componentes e a câmera
        this.emailUsuario = email;

        setTitle("Autenticação Facial");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Utilização de face detector externo
        faceDetector = new CascadeClassifier("D:\\Code\\maven\\projeto-aps\\src\\main\\resources\\haarcascade_frontalface_alt.xml");
        iniciarComponentes();
        iniciarCamera();
    }

    private void iniciarComponentes() {
        // Cria e configura os componentes da interface gráfica
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));

        lblCamera = new JLabel();
        lblCamera.setHorizontalAlignment(JLabel.CENTER);
        lblCamera.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 4));
        panel.add(lblCamera, BorderLayout.CENTER);

        btnAutenticar = new JButton("Autenticar");
        btnAutenticar.setBackground(new Color(30, 144, 255));
        btnAutenticar.setForeground(Color.WHITE);
        btnAutenticar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAutenticar.setFocusPainted(false);
        btnAutenticar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAutenticar.addActionListener(e -> autenticarImagem());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(panel.getBackground());
        bottomPanel.add(btnAutenticar);

        JLabel lblTitulo = new JLabel("Autenticação Facial");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void iniciarCamera() {
        // Inicia a câmera e exibe a captura de vídeo na tela
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

    private void autenticarImagem() {
        // Processa a imagem capturada e realiza a autenticação biométrica
        if (!frame.empty()) {
            Mat rostoDetectado = detectarRosto(frame);

            if (rostoDetectado == null) {
                JOptionPane.showMessageDialog(this, "Nenhum rosto detectado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String caminhoImagemSalva = "imagens/" + emailUsuario + ".png";
            Mat imagemSalva = Imgcodecs.imread(caminhoImagemSalva);

            if (imagemSalva.empty()) {
                JOptionPane.showMessageDialog(this, "Imagem de cadastro não encontrada. Vamos cadastrar sua biometria.", "Cadastro Necessário", JOptionPane.INFORMATION_MESSAGE);

                // Chama a tela de cadastro de biometria para capturar a foto
                TelaCadastroBiometria telaCadastro = new TelaCadastroBiometria(emailUsuario);
                telaCadastro.setVisible(true);
                dispose(); // Fecha a tela de autenticação

                return;
            }

            Mat imagemCinzaAtual = new Mat();
            Mat imagemCinzaSalva = new Mat();
            Imgproc.cvtColor(rostoDetectado, imagemCinzaAtual, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(imagemSalva, imagemCinzaSalva, Imgproc.COLOR_BGR2GRAY);

            if (compararImagens(imagemCinzaAtual, imagemCinzaSalva)) {
                int nivelPermissao = BancoDados.obterNivelPermissao(emailUsuario);

                JOptionPane.showMessageDialog(this, "Autenticação bem-sucedida!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                TelaPrincipal telaPrincipal = new TelaPrincipal(nivelPermissao);
                telaPrincipal.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Autenticação falhou. Imagem não corresponde.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao capturar a imagem.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Detecta o rosto na imagem capturada
    private Mat detectarRosto(Mat imagem) {
        MatOfRect rostosDetectados = new MatOfRect();
        faceDetector.detectMultiScale(imagem, rostosDetectados);

        for (Rect rect : rostosDetectados.toArray()) {
            return new Mat(imagem, rect);
        }
        return null;
    }

    // Compara duas imagens e verifica a similaridade entre elas
    private boolean compararImagens(Mat imagemAtual, Mat imagemSalva) {
        Mat imagemAtualRedimensionada = new Mat();
        Imgproc.resize(imagemAtual, imagemAtualRedimensionada, imagemSalva.size());

        Mat diff = new Mat();
        Core.absdiff(imagemAtualRedimensionada, imagemSalva, diff);
        Core.multiply(diff, diff, diff);

        double somatorio = Core.sumElems(diff).val[0];
        double totalPixels = imagemAtualRedimensionada.total();

        double mediaDiferenca = somatorio / totalPixels;

        return mediaDiferenca < 1000;
    }

    // Converte um objeto Mat (OpenCV) em BufferedImage para exibição
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
        // Libera a câmera ao fechar a aplicação
        camera.release();
        super.dispose();
    }
}
