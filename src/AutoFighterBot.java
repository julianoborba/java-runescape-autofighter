import java.awt.AWTException;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.sun.awt.AWTUtilities;

public class AutoFighterBot {
	
	private static final AutoColorClick autoFighter = new AutoColorClick();
	private final JFrame mainWindow = new JFrame("AutoFighter/ColorClick v2.1.3");
	private final JFrame locateRuneScapeWindow = new JFrame("Captura de posicionamento da tela");
	private static final JTextField txtAtraso = new JTextField();
	private static final JTextField txtCorEscrita = new JTextField();
	private static final JTextField txtDesligamentoAgendado = new JTextField();
	private final JButton btnParar = new JButton("Parar");
	private final JButton btnCapturarPosicaoTela = new JButton("Capturar posição da tela");
	private final JButton btnAtacar = new JButton("Atacar");
	private final JButton btnNovaCor = new JButton("Nova cor");
	private final JButton btnColecaoDeCores = new JButton("Col. cores");
	private final JButton btnAtrase = new JButton("Atrasar");
	private final JButton btnAtrasoRandom = new JButton("Atraso random.");
	private final JButton btnDesligamentoAgendado = new JButton("Desl. agendado");
	private static final JLabel lblCor = new JLabel("Status cor");
	private final JLabel lblAtrasoRandomAtiv = new JLabel("Desativado");
	private final JLabel lblXinicial = new JLabel(" X ini : 0");
	private final JLabel lblXfinal = new JLabel(" X fim : 0");
	private final JLabel lblYinicial = new JLabel(" Y ini : 0");
	private final JLabel lblYfinal = new JLabel(" Y fim : 0");
	private final JLabel lblTempo = new JLabel("0 minuto(s)");
	private long cron = 0;

	public AutoFighterBot() {
		
		final String textoEtiquetaCor = "Status cor";
		final String textoCorEscrita = "rrr,ggg,bbb";
		final String textoDesligamentoAgendado = "mm,hh,DD";
		final String textoAtraso = "Atraso em milisegundos";
		
		
		mainWindow.setLayout(null);
		mainWindow.setLocation(6, 150);
		mainWindow.setSize(265, 275);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setResizable(false);
		mainWindow.setAlwaysOnTop(true);
		
		
		locateRuneScapeWindow.setLayout(null);
		locateRuneScapeWindow.setLocation(6, 425);
		locateRuneScapeWindow.setSize(265, 180);
		locateRuneScapeWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		locateRuneScapeWindow.setResizable(true);
		locateRuneScapeWindow.setAlwaysOnTop(true);
		AWTUtilities.setWindowOpacity(locateRuneScapeWindow, 0.6F);
		
		
		txtAtraso.setLocation(10, 110);
		txtAtraso.setSize(148, 31);
		txtAtraso.setText(textoAtraso);
		txtAtraso.setHorizontalAlignment(JTextField.CENTER);
		txtAtraso.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent ev) {
				try {
					if (!(Integer.parseInt(txtAtraso.getText()) >= 1))
						txtAtraso.setText(textoAtraso);
				} catch (Exception e) {
					txtAtraso.setText(textoAtraso);
				}
			}
			@Override
			public void focusGained(FocusEvent ev) {
				txtAtraso.setText("");
			}
		});
		
		
		btnAtrase.setLocation(155, 110);
		btnAtrase.setSize(90, 30);
		btnAtrase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				configuraAtraso(autoFighter, txtAtraso);
				JOptionPane.showMessageDialog(mainWindow, "Novo delay : " + autoFighter.getDelay());
			}
		});
		
		
		btnAtrasoRandom.setLocation(110, 80);
		btnAtrasoRandom.setSize(135, 30);
		btnAtrasoRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (autoFighter.isRandomClick()) {
					autoFighter.setRandomClick(false);
					lblAtrasoRandomAtiv.setText("Desativado");
				} else {
					autoFighter.setRandomClick(true);
					lblAtrasoRandomAtiv.setText("Ativado");
				}
			}
		});
		
		
		lblAtrasoRandomAtiv.setLocation(10, 80);
		lblAtrasoRandomAtiv.setSize(100, 30);
		lblAtrasoRandomAtiv.setBorder(BorderFactory.createLineBorder(Color.black)); 
		lblAtrasoRandomAtiv.setHorizontalAlignment(JTextField.CENTER);
		
		
		btnAtacar.setLocation(10, 10);
		btnAtacar.setSize(90, 30);
		btnAtacar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				
				boolean start = true;
				if (!autoFighter.isAlive()) {
					configuraAtraso(autoFighter, txtAtraso);
					try {
						if (autoFighter.getObjectColor() == null) {
							JOptionPane.showMessageDialog(mainWindow, "Defina uma cor!");
							start = false;
						} else {
							start = true;
						}
						if (autoFighter.getScreenBeginX() <= 0 || autoFighter.getScreenEndX() <=0 || autoFighter.getScreenBeginY() <= 0 || autoFighter.getScreenEndY() <= 0) {
							JOptionPane.showMessageDialog(mainWindow, "Defina uma posição na tela!");
							start = false;
						} else {
							start = true;
						}
					}catch (Exception e) {
						start = false;
					}
					if (start) {
						autoFighter.start();
						btnParar.setEnabled(true);
						btnAtacar.setEnabled(false);
						iniciaCronometro();
					}
				}
				if (autoFighter.isStop()) {
					autoFighter.setStop(false);
					btnParar.setEnabled(true);
					btnAtacar.setEnabled(false);
				}
			}
		});
		
		
		btnParar.setLocation(10, 39);
		btnParar.setSize(90, 29);
		btnParar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (autoFighter.isAlive()) {
					autoFighter.setStop(true);
					btnParar.setEnabled(false);
					btnAtacar.setEnabled(true);
				}
			}
		});
		
		
		btnNovaCor.setLocation(10, 179);
		btnNovaCor.setSize(100, 30);
		btnNovaCor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String temp = textoCorEscrita;
				autoFighter.setColorColection(false);
				autoFighter.setObjectColorColection(null);
				try {
					if (!txtCorEscrita.getText().equals("") && !txtCorEscrita.getText().equals(temp) && txtCorEscrita.getText().matches("^(\\d{3},\\d{3},\\d{3})$")) {
						try {
							String[] sRGB = txtCorEscrita.getText().split(",");
							if (sRGB.length == 3) {
								Integer[] rgb = new Integer[3];
								for (int i = 0; i < rgb.length; i++) rgb[i] = Integer.parseInt(sRGB[i]);
								autoFighter.setObjectColor(new Color(rgb[0], rgb[1], rgb[2]));
							} else {
								txtCorEscrita.setText(temp);
								configuraCorMonstroPorClick();
							}
						} catch (Exception e) {
							txtCorEscrita.setText(temp);
							configuraCorMonstroPorClick();
						}
						lblCor.setBackground(autoFighter.getObjectColor());
						String rgb = " [R " + autoFighter.getObjectColor().getRed() + "]" + " [G " + autoFighter.getObjectColor().getGreen() + "]" + " [B " + autoFighter.getObjectColor().getBlue() + "]";
						lblCor.setText(rgb);
					} else {
						txtCorEscrita.setText(temp);
						configuraCorMonstroPorClick();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		});
		
		
		btnColecaoDeCores.setLocation(10, 150);
		btnColecaoDeCores.setSize(100, 30);
		btnColecaoDeCores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (autoFighter.isColorColection()) {
					autoFighter.setColorColection(false);
					autoFighter.setObjectColorColection(null);
					if (autoFighter.getObjectColor() != null) {
						String rgb = " [R " + autoFighter.getObjectColor().getRed() + "]" + " [G " + autoFighter.getObjectColor().getGreen() + "]" + " [B " + autoFighter.getObjectColor().getBlue() + "]";
						lblCor.setText(rgb);
					} else {
						lblCor.setText(textoEtiquetaCor);
					}
				} else {
					try{
						if (autoFighter.getObjectColor() != null) {
							autoFighter.setColorColection(true);
							List<Color> colorColection = new ArrayList<Color>();
							File inputFile = new File(JOptionPane.showInputDialog(mainWindow, "Defina o caminho para\numa imagem. Esta será escaneada.\n\n", "Aviso", JOptionPane.INFORMATION_MESSAGE));
							BufferedImage bufferedImage = ImageIO.read(inputFile);
							int w = bufferedImage.getWidth();
							int h = bufferedImage.getHeight();
							for (int x = 0; x < w; x++) {
								for (int y = 0; y < h; y++) {
									Color color = new Color(bufferedImage.getRGB(x, y));
									if (color.getRed() != 255 && color.getGreen() != 255 && color.getBlue() != 255 && color.getRed() != 000 && color.getGreen() != 000 && color.getBlue() != 000 && !colorColection.contains(color)) {
										colorColection.add(color);
									}
								}
							}
							JOptionPane.showMessageDialog(mainWindow, "Imagem escaneada!");
							autoFighter.setObjectColorColection(colorColection);
							lblCor.setText("Col. ativada");
						} else {
							JOptionPane.showMessageDialog(mainWindow, "Por segurança, escolha\npelo menos uma cor inicial\n\n");
							lblCor.setText(textoEtiquetaCor);
						}
					} catch (Exception ex) {
						autoFighter.setColorColection(false);
						autoFighter.setObjectColorColection(null);
						if (autoFighter.getObjectColor() != null) {
							String rgb = " [R " + autoFighter.getObjectColor().getRed() + "]" + " [G " + autoFighter.getObjectColor().getGreen() + "]" + " [B " + autoFighter.getObjectColor().getBlue() + "]";
							lblCor.setText(rgb);
						} else {
							lblCor.setText(textoEtiquetaCor);
						}
					}
					
				}
			}
		});
		
		
		lblCor.setLocation(110, 150);
		lblCor.setSize(135, 30);
		lblCor.setOpaque(true);
		lblCor.setBorder(BorderFactory.createLineBorder(Color.black)); 
		lblCor.setBackground(Color.LIGHT_GRAY);
		lblCor.setHorizontalAlignment(JTextField.CENTER);
		
		
		txtCorEscrita.setLocation(110, 180);
		txtCorEscrita.setSize(135, 30);
		txtCorEscrita.setText(textoCorEscrita);
		txtCorEscrita.setHorizontalAlignment(JTextField.CENTER);
		txtCorEscrita.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent ev) {
				if (txtCorEscrita.getText().equals(""))
					txtCorEscrita.setText(textoCorEscrita);
			}
			@Override
			public void focusGained(FocusEvent ev) {
				if (txtCorEscrita.getText().equals(textoCorEscrita)) {
					txtCorEscrita.setText("");
				}
			}
		});
		
		
		btnCapturarPosicaoTela.setLocation(10, 10);
		btnCapturarPosicaoTela.setSize(200, 30);
		btnCapturarPosicaoTela.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int screenBeginX = (int)Math.round(locateRuneScapeWindow.getLocation().getX());
				int screenEndX = ((int)Math.round(locateRuneScapeWindow.getLocation().getX()))+(int)Math.round(locateRuneScapeWindow.getSize().getWidth());
				int screenBeginY = (int)Math.round(locateRuneScapeWindow.getLocation().getY());
				int screenEndY = ((int)Math.round(locateRuneScapeWindow.getLocation().getY())+(int)Math.round(locateRuneScapeWindow.getSize().getHeight()));
				autoFighter.setScreenBeginX(screenBeginX);
				autoFighter.setScreenEndX(screenEndX);
				autoFighter.setScreenBeginY(screenBeginY);
				autoFighter.setScreenEndY(screenEndY);
				lblXinicial.setText("X ini : " + screenBeginX);
				lblXfinal.setText  ("X fim : " + screenEndX);
				lblYinicial.setText("Y ini : " + screenBeginY);
				lblYfinal.setText  ("Y fim : " + screenEndY);
				autoFighter.setNewScreen(true);
			}
		});
		
		
		btnDesligamentoAgendado.setLocation(110, 10);
		btnDesligamentoAgendado.setSize(140, 30);
		btnDesligamentoAgendado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String temp = textoDesligamentoAgendado;
				try {
					if (!txtDesligamentoAgendado.getText().equals("") && !txtDesligamentoAgendado.getText().equals(temp) && txtDesligamentoAgendado.getText().matches("^(\\d{2},\\d{2},\\d{2})$")) {
						try {
							String[] sMhd = txtDesligamentoAgendado.getText().split(",");
							if (sMhd.length == 3) {
								Integer[] mhd = new Integer[3];
								for (int i = 0; i < mhd.length; i++)
									mhd[i] = Integer.parseInt(sMhd[i]);
								autoFighter.setMin(mhd[0]);
								autoFighter.setHour(mhd[1]);
								autoFighter.setDay(mhd[2]);
								JOptionPane.showMessageDialog(mainWindow, "Desligamento agendado para\nDia " + autoFighter.getDay() + " deste mês\nÁs " + autoFighter.getHour() + ":" + autoFighter.getMin() + "h");
							} else {
								txtDesligamentoAgendado.setText(temp);
							}
						} catch (Exception e) {
							txtDesligamentoAgendado.setText(temp);
						}
					} else {
						txtDesligamentoAgendado.setText(temp);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					txtDesligamentoAgendado.setText(temp);
				}
			}
		});
		
		
		txtDesligamentoAgendado.setLocation(110, 39);
		txtDesligamentoAgendado.setSize(140, 30);
		txtDesligamentoAgendado.setText(textoDesligamentoAgendado);
		txtDesligamentoAgendado.setHorizontalAlignment(JTextField.CENTER);
		txtDesligamentoAgendado.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent ev) {
				if (txtDesligamentoAgendado.getText().equals(""))
					txtDesligamentoAgendado.setText(textoDesligamentoAgendado);
			}
			@Override
			public void focusGained(FocusEvent ev) {
				if (txtDesligamentoAgendado.getText().equals(textoDesligamentoAgendado))
					txtDesligamentoAgendado.setText("");
			}
		});
		
		
		lblXinicial.setLocation(10, 50);
		lblXfinal.setLocation(135, 50);
		lblYinicial.setLocation(10, 100);
		lblYfinal.setLocation(135, 100);
		
		lblXinicial.setSize(135, 30);
		lblXfinal.setSize(135, 30);
		lblYinicial.setSize(135, 30);
		lblYfinal.setSize(135, 30);


		lblTempo.setLocation(10, 220);
		lblTempo.setSize(235, 20);
		lblTempo.setBorder(BorderFactory.createLineBorder(Color.black)); 
		lblTempo.setHorizontalAlignment(JTextField.CENTER);
		
		
		mainWindow.add(lblCor);
		mainWindow.add(lblAtrasoRandomAtiv);
		mainWindow.add(lblTempo);
		mainWindow.add(btnAtacar);
		mainWindow.add(btnParar);
		mainWindow.add(btnAtrase);
		mainWindow.add(btnNovaCor);
		mainWindow.add(btnDesligamentoAgendado);
		mainWindow.add(btnAtrasoRandom);
		mainWindow.add(txtAtraso);
		mainWindow.add(txtCorEscrita);
		mainWindow.add(txtDesligamentoAgendado);
		mainWindow.add(btnColecaoDeCores);
		
		
		locateRuneScapeWindow.add(btnCapturarPosicaoTela);
		locateRuneScapeWindow.add(lblXinicial);
		locateRuneScapeWindow.add(lblXfinal);
		locateRuneScapeWindow.add(lblYinicial);
		locateRuneScapeWindow.add(lblYfinal);
		
		
		mainWindow.setVisible(true);
		locateRuneScapeWindow.setVisible(true);
	}

	private void configuraAtraso(final AutoColorClick autoFighter, final JTextField txtAtraso) {
		try {
			if (Integer.parseInt(txtAtraso.getText()) >= 1)
				autoFighter.setDelay(Integer.parseInt(txtAtraso.getText()));
		} catch (Exception e) {
			autoFighter.setDelay(9000);
		}
	}
	
	private void configuraCorMonstroPorClick() throws AWTException, HeadlessException {
		Robot catchColor = new Robot();
		JOptionPane.showMessageDialog(mainWindow, "Você tem 3 segundos para\nposicionar o mouse sobre\na cor do monstro...");
		catchColor.delay(3000);
		Point point = new Point();
		point = MouseInfo.getPointerInfo().getLocation();
		int x = (int) point.getX();
		int y = (int) point.getY();
		autoFighter.setObjectColor(catchColor.getPixelColor(x, y));
		JOptionPane.showMessageDialog(mainWindow, "Cor setada!");
		String rgb = " [R " + autoFighter.getObjectColor().getRed() + "]" + " [G " + autoFighter.getObjectColor().getGreen() + "]" + " [B " + autoFighter.getObjectColor().getBlue() + "]";
		lblCor.setBackground(autoFighter.getObjectColor());
		lblCor.setText(rgb);
	}
	
	public void iniciaCronometro() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					cron++;
					lblTempo.setText((cron/60) + " minuto(s)");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	
	public static void main(String args[]) {
		String path = JOptionPane.showInputDialog(null, "Se desejar, aponte o local\nonde existe o arquivo\nde configuração\n\n", "Aviso", JOptionPane.INFORMATION_MESSAGE);
		try {
			if (path != null) {
				new AutoFighterBot();
				BufferedReader reader = new BufferedReader(new FileReader(new File(path + "\\conf.txt")));
				String line = "", aux = "";
				for (int i = 0; i < 3 && (line = reader.readLine()) != null; i++) {
					switch (i) {
						case 0:
							aux = line.split(":")[1];
							autoFighter.setMin(Integer.parseInt(aux.split(",")[0]));
							autoFighter.setHour(Integer.parseInt(aux.split(",")[1]));
							autoFighter.setDay(Integer.parseInt(aux.split(",")[2]));				
							break;
						case 1:
							aux = line.split(":")[1];
							autoFighter.setDelay(Integer.parseInt(aux));
							break;
						case 2:
							aux = line.split(":")[1];
							autoFighter.setObjectColor(new Color(
									Integer.parseInt(aux.split(",")[0]),
									Integer.parseInt(aux.split(",")[1]),
									Integer.parseInt(aux.split(",")[2])
							));
							lblCor.setBackground(new Color(
									Integer.parseInt(aux.split(",")[0]),
									Integer.parseInt(aux.split(",")[1]),
									Integer.parseInt(aux.split(",")[2])
							));
							break;
						default:
							break;
					}
				}
				reader.close();
				StringBuffer sb = new StringBuffer("");
				txtAtraso.setText(String.valueOf(autoFighter.getDelay()));
				sb.append("[R " + autoFighter.getObjectColor().getRed() + "] ");
				sb.append("[G " + autoFighter.getObjectColor().getGreen() + "] ");
				sb.append("[B " + autoFighter.getObjectColor().getBlue() + "]");
				lblCor.setText(sb.toString());
				sb.delete(0, sb.length());
				sb.append(autoFighter.getMin() + ",");
				sb.append(autoFighter.getHour() + ",");
				sb.append(autoFighter.getDay());
				txtDesligamentoAgendado.setText(sb.toString());
			} else {
				new AutoFighterBot();
			}
		} catch (Exception e) {
			new AutoFighterBot();
		}
	}
	
}