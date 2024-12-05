import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CalculadoraPantalla extends JFrame {

    // Creamos una variable global para el JTextArea
    private JTextArea areaTexto;
    private JTextArea areaAlmacenado; // Sección para mostrar el dato almacenado
    private boolean tecladoOn = true; // activa el teclado
    private boolean buttonOn = true; // activa el pulso de botones
    private int[] cont = { 0, 1, 2 }; // Lista de posibles modos
    private int modo = 0; // inicia en modo multi
    private KeyAdapter tecladoListener; // Referencia al KeyListener

    public CalculadoraPantalla() {
        super("Calculadora de Víctor Modo Boton");

        // Configuración del diseño principal
        setLayout(new BorderLayout());
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // Tamaño de pantalla

        // Configuración del área de texto principal
        areaTexto = new JTextArea();
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setFont(new Font("Arial", Font.PLAIN, 95));
        areaTexto.setEditable(false);

        JScrollPane scrollTexto = new JScrollPane(areaTexto);

        // Configuración del área para el dato almacenado
        areaAlmacenado = new JTextArea();
        areaAlmacenado.setLineWrap(true);
        areaAlmacenado.setWrapStyleWord(true);
        areaAlmacenado.setFont(new Font("Arial", Font.PLAIN, 50));
        areaAlmacenado.setEditable(false);

        JScrollPane scrollAlmacenado = new JScrollPane(areaAlmacenado);

        // Crear un panel que contiene ambas áreas de texto
        JPanel panelPantallas = new JPanel(new GridLayout(2, 1)); // Dos filas: una para cada área
        panelPantallas.add(scrollTexto); // Área principal arriba
        panelPantallas.add(scrollAlmacenado); // Área de almacenado abajo

        // Panel para los botones numéricos (4x3)
        JPanel panelNumeros = new JPanel(new GridLayout(4, 3, 5, 5));
        String[] botonesNumericos = {
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "0", "C", "Change Mode"
        };

        for (String texto : botonesNumericos) {
            Button button = new Button(texto);
            button.setFont(new Font("Arial", Font.PLAIN, 20)); // cambia la fuente y el tamaño de letra
            button.setBackground(!texto.equals("C") ? Color.WHITE : Color.GREEN); // cambios especificos para button C
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manejarAccionBoton(texto);
                }
            });
            panelNumeros.add(button);
        }

        // Panel para las operaciones (6x1)
        JPanel panelOperaciones = new JPanel(new GridLayout(6, 1, 5, 5));
        String[] botonesOperaciones = { "/", "*", "-", "+", ",", "=" };

        for (String texto : botonesOperaciones) {
            Button button = new Button(texto);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.setBackground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manejarAccionBoton(texto);
                }
            });
            panelOperaciones.add(button);
        }

        // Panel inferior que combina botones numéricos y operaciones
        JPanel panelBotones = new JPanel(new BorderLayout());
        panelBotones.add(panelNumeros, BorderLayout.CENTER); // Botones numéricos en el centro
        panelBotones.add(panelOperaciones, BorderLayout.EAST); // Operaciones a la derecha

        // Agregar los paneles al JFrame
        add(panelPantallas, BorderLayout.NORTH); // Panel con las áreas de texto en la parte superior
        add(panelBotones, BorderLayout.CENTER); // Panel con los botones en la parte inferior

        // Configuración de la ventana
        setSize(d.width / 2, 600); // Tamaño inicial
        setResizable(true); // Permite redimensionar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cerrar al salir

        // Ajuste dinámico del tamaño
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ajustarComponentes();
            }
        });
    }

    // Ajuste dinámico de los componentes
    private void ajustarComponentes() {
        Dimension size = getSize();
        int panelWidth = size.width;
        int panelHeight = size.height / 2;

        areaTexto.setPreferredSize(new Dimension(panelWidth, panelHeight / 4));
        areaAlmacenado.setPreferredSize(new Dimension(panelWidth, panelHeight / 8));

        repaint();
    }

    private void manejarAccionBoton(String texto) {
        if (texto.equals("C")) { // Si se presiona el boton C
            areaTexto.setText(""); // Borra el contenido del JTextArea
        } else if (texto.equals("=") && buttonOn) {
            manejarLaOperacion(); // Llamada para manejar la operación
        } else if(texto.equals("Change Mode")){

            if(modo == 2){
                modo = 0;
            }else{
                modo++;
            }
            if(cont[modo] == 0){
                buttonOn = true;
                tecladoOn = false;
                setTitle("Calculadora Victor Modo Multi");
            }else if(cont[modo] == 1){
                buttonOn = false;
                tecladoOn = false;
                setTitle("Calculadora Victor Modo Botón");
            }else if(cont[modo] == 2){
                buttonOn = false;
                tecladoOn = true;
                setTitle("Calculadora Victor Modo Teclado");
            }
            if (tecladoOn) {
                areaTexto.removeKeyListener(tecladoListener);
                areaTexto.addKeyListener(tecladoListener);
            } else {
                areaTexto.removeKeyListener(tecladoListener); // Elimina correctamente el KeyListener
                areaTexto.removeKeyListener(tecladoListener);
            }
        }else if(buttonOn){
            areaTexto.append(texto); // Añade el texto del botón presionado
        }
    }

    public void manejarLaOperacion() {
        String texto = areaTexto.getText(); // Obtener el texto actual en el JTextArea
        if(texto.contains("Error")){
            texto.replace("Error", "");
        }
        if (texto.isEmpty()) { //Si no hay nada escrito
            return; // retorna
        }
    
        try {
            texto = calcularOperacion(texto, "*/"); // comprueba primero si hay multiplicación o división y actualiza el contenido del texto
    
            texto = calcularOperacion(texto, "+-"); // Luego hace las sumas y restas y actualiza el valor de texto
    
            // Si el resultado es negativo, cambia el color del texto en el área de texto almacenado
            if (Double.parseDouble(texto.replace(',', '.')) < 0) {
                areaAlmacenado.setForeground(Color.RED); // Cambia el color a rojo
            } else {
                areaAlmacenado.setForeground(Color.BLACK); // Si no es negativo, lo pone negro
            }
    
            areaAlmacenado.setText(texto); // Muestra el resultado en el área de texto almacenado
    
        } catch (Exception e) {
            areaAlmacenado.setText("Error"); // Si ocurre un error en el proceso
            areaAlmacenado.setForeground(Color.RED); // Cambia el color a rojo para el mensaje de error
            try {
                Thread.sleep(1000); // pausa de 1 segundo
                areaAlmacenado.setText(""); // Si ocurre un error, limpia el texto
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    
    private String calcularOperacion(String texto, String operadores) {
        String regex = "(-?\\d+(?:[.,]\\d+)?)([" + operadores + "])(-?\\d+(?:[.,]\\d+)?)"; // busca el operador, detecta el numero de repeticiones
    
        Pattern pattern = Pattern.compile(regex);   // crea un patrón que busca los operadores dados como parámetros
        Matcher matcher = pattern.matcher(texto);   // busca un determinado patrón en el área de texto
    
        DecimalFormat df = new DecimalFormat("#.##");   // formatea el número que contiene .
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();      // crea un simbolo para formatear
        symbols.setDecimalSeparator(',');   // setea la sepacion decimal con ,
        df.setDecimalFormatSymbols(symbols);    // cambia el . por ,
    
        while (matcher.find()) {
            // Convertir los números a formato estándar para cálculo
            double num1 = Double.parseDouble(matcher.group(1).replace(',', '.')); // obtiene el primer número
            String operador = matcher.group(2); // obtiene el operador
            double num2 = Double.parseDouble(matcher.group(3).replace(',', '.')); // obtiene el segundo número
            double resultado = 0;   // inicializa la variable resultado en 0
    
            // Realizar la operación
            switch (operador) {
                case "*":
                    resultado = num1 * num2;    //Hoy no dormirás sabiendo esto, el num1 se multiplica al num2, inesperado, ¿a que sí?
                    break;
                case "/":
                    if (num2 == 0) {
                        throw new ArithmeticException("División por cero"); // causa de error: Division indeterminada
                    }
                    resultado = num1 / num2;     // para sorpresa de nadie, hace una división
                    break;
                case "+":
                    resultado = num1 + num2;    // quizás te impacte saber esto, pero el num1 se suma al num2, impresionante, ¿verdad?
                    break;
                case "-":
                    resultado = num1 - num2;     // el impacto de tan gran línea de código te lena de determinación. Resta dos números. ¡Alucinante!
                    break;
            }
    
            texto = texto.substring(0, matcher.start()) + resultado + texto.substring(matcher.end()); // Reemplaza la operación por el resultado
    
            matcher = pattern.matcher(texto); // Vuelve a buscar el siguiente patrón en el área de texto
        }
    
        return texto.replace('.', ','); // Reemplaza el punto por coma para la visualización final
    }

    public void activarTeclado() {
        if (tecladoListener == null) {
            tecladoListener = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode(); // Obtiene el código de la tecla
                    if (isValidKey(keyCode)) {
                        String keyText = "";
    
                        // Convertir las teclas numéricas y operadores a texto
                        if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
                            keyText = String.valueOf(keyCode - KeyEvent.VK_NUMPAD0);
                        } else if (keyCode == KeyEvent.VK_ADD) {
                            keyText = "+";
                        } else if (keyCode == KeyEvent.VK_SUBTRACT) {
                            keyText = "-";
                        } else if (keyCode == KeyEvent.VK_MULTIPLY) {
                            keyText = "*";
                        } else if (keyCode == KeyEvent.VK_DIVIDE) {
                            keyText = "/";
                        } else if (keyCode == KeyEvent.VK_DECIMAL) {
                            keyText = ",";
                        } else if (keyCode == KeyEvent.VK_ENTER) {
                            manejarLaOperacion(); // Si se presiona enter, realiza la operación
                            return;
                        }
    
                        areaTexto.append(keyText); // Añade el texto del botón presionado al área de texto
                    }
                    e.consume(); // Evita que el teclado realice la acción predeterminada
                }
    
                private boolean isValidKey(int keyCode) {
                    return (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9)
                            || keyCode == KeyEvent.VK_ADD
                            || keyCode == KeyEvent.VK_SUBTRACT
                            || keyCode == KeyEvent.VK_MULTIPLY
                            || keyCode == KeyEvent.VK_DIVIDE
                            || keyCode == KeyEvent.VK_ENTER
                            || keyCode == KeyEvent.VK_DECIMAL;
                }
            };
        }
    
        if (tecladoOn) {
            areaTexto.addKeyListener(tecladoListener);
        } else {
            areaTexto.removeKeyListener(tecladoListener);
        }
    }
    

    public static void main(String[] args) {
        CalculadoraPantalla ventana = new CalculadoraPantalla();
        ventana.setVisible(true); // Hace visible la ventana
    }
}
