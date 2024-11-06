package com.aps;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

public class TelaAutenticacaoBiometria extends JFrame {

    static {
        // Carrega a biblioteca OpenCV
        System.load("D:\\Code\\maven\\projeto-aps\\src\\main\\resources\\opencv_java490.dll");
    }

    private JLabel lblCamera;
    private JButton btnAutenticar, btnVoltarLogin;
    private VideoCapture camera;
    private Mat frame;
    private String emailUsuario;
    private CascadeClassifier faceDetector;

    public TelaAutenticacaoBiometria(String email) {
        // Configura a janela de autenticação e inicializa componentes e a câmera
        this.emailUsuario = email;

        setTitle("Autenticação Facial");
        setSize(800, 600); // Aumenta o tamanho da tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Utilização de face detector externo
        faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_alt.xml");
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

        btnVoltarLogin = new JButton("Voltar para Login");
        btnVoltarLogin.setBackground(new Color(255, 69, 0)); // Cor laranja
        btnVoltarLogin.setForeground(Color.WHITE);
        btnVoltarLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVoltarLogin.setFocusPainted(false);
        btnVoltarLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVoltarLogin.addActionListener(e -> voltarParaLogin());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(panel.getBackground());
        bottomPanel.add(btnAutenticar);
        bottomPanel.add(btnVoltarLogin);

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

                TelaPrincipal telaPrincipal = new TelaPrincipal(nivelPermissao, emailUsuario);
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
        // Redimensiona a imagem atual para o tamanho da imagem salva
        Mat imagemAtualRedimensionada = new Mat();
        Imgproc.resize(imagemAtual, imagemAtualRedimensionada, imagemSalva.size());

        // Converte as imagens para escala de cinza (se não estiverem já em escala de cinza)
        Mat imagemAtualCinza = new Mat();
        Mat imagemSalvaCinza = new Mat();

        // Verifica se a imagem já está em escala de cinza
        if (imagemAtualRedimensionada.channels() > 1) {
            Imgproc.cvtColor(imagemAtualRedimensionada, imagemAtualCinza, Imgproc.COLOR_BGR2GRAY);
        } else {
            imagemAtualCinza = imagemAtualRedimensionada;
        }

        if (imagemSalva.channels() > 1) {
            Imgproc.cvtColor(imagemSalva, imagemSalvaCinza, Imgproc.COLOR_BGR2GRAY);
        } else {
            imagemSalvaCinza = imagemSalva;
        }

        // Calcula a diferença absoluta entre as duas imagens
        Mat diff = new Mat();
        Core.absdiff(imagemAtualCinza, imagemSalvaCinza, diff);

        // Calcula a soma dos elementos da imagem de diferença
        double somatorio = Core.sumElems(diff).val[0];

        // Normaliza a soma pela quantidade de pixels
        double totalPixels = diff.total();
        double mediaDiferenca = somatorio / totalPixels;

        // Se a média de diferença for maior que um limiar, as imagens são diferentes
        double limiar = 30;  // Ajuste conforme necessário para a sensibilidade
        if (mediaDiferenca > limiar) {
            return false; // As imagens são diferentes
        }

        // Alternativamente, podemos comparar os histogramas para um método mais robusto
        java.util.List<Mat> imagens = new ArrayList<>();
        imagens.add(imagemAtualCinza);
        imagens.add(imagemSalvaCinza);

        Mat histAtual = new Mat();
        Mat histSalvo = new Mat();

        // Calcula o histograma para cada imagem
        Imgproc.calcHist(imagens, new MatOfInt(0), new Mat(), histAtual, new MatOfInt(256), new MatOfFloat(0f, 256f));
        Imgproc.calcHist(imagens, new MatOfInt(0), new Mat(), histSalvo, new MatOfInt(256), new MatOfFloat(0f, 256f));

        // Normaliza os histogramas
        Core.normalize(histAtual, histAtual, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(histSalvo, histSalvo, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // Calcula a correlação entre os dois histogramas
        double correlacao = Imgproc.compareHist(histAtual, histSalvo, Imgproc.CV_COMP_CORREL);

        // Se a correlação for maior que 0.7, as imagens são semelhantes
        return correlacao > 0.7;
    }

    private void voltarParaLogin() {
        // Volta para a tela de login
        TelaLogin telaLogin = new TelaLogin();
        telaLogin.setVisible(true);
        dispose();
    }

    // Converte uma imagem do tipo Mat para Image
    private Image convertMatToImage(Mat frame) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (frame.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage imagem = new BufferedImage(frame.width(), frame.height(), type);
        frame.get(0, 0, ((DataBufferByte) imagem.getRaster().getDataBuffer()).getData());
        return imagem;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAutenticacaoBiometria("usuario@example.com").setVisible(true));
    }
}
