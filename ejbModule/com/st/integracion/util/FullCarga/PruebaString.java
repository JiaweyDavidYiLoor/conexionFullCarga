package com.st.integracion.util.FullCarga;

public class PruebaString {

	/**
	 * @param args
	 */
	
	private static final String ORIGINAL
    = "¡·…ÈÕÌ”Û⁄˙—Ò‹¸";
private static final String REPLACEMENT
    = "AaEeIiOoUuNnUu";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		/* String palabra = "C2017071100000001000000000000339";
      
      String codigo = palabra.substring(0, 1);
      System.out.println("codigo="+codigo);      
      String fecha = palabra.substring(1,9);
      System.out.println("fecha="+fecha);
	  String numeroRegistro = palabra.substring(9,17);
	  System.out.println("numeroRegistro="+numeroRegistro);	
	  String valorTotal = palabra.substring(17,32);
	  System.out.println("valorTotal="+valorTotal);
	  
	  
	  String palabra2 = "D201707122110100000016EPMAPS         4AU        20170711010010103081174             ANDRADE JACOME MERCEDES                                     000000000000339111131220000001";
	  
	  String codigoD = palabra2.substring(0, 1);
      System.out.println("codigo="+codigoD);      
      
      String fechaD = palabra2.substring(1,9);
      System.out.println("fecha="+fechaD);
      
      String horaTransaccion = palabra2.substring(9,15);
      System.out.println("horaTransaccion="+horaTransaccion);
      
      String numeroTransaccion = palabra2.substring(15,22);
      System.out.println("numeroTransaccion="+numeroTransaccion);
      
      String empresa = palabra2.substring(22,37);
      System.out.println("empresa="+empresa);
      
      String producto = palabra2.substring(37,38);
      System.out.println("producto="+producto);
      
      String servicio = palabra2.substring(38,48);
      System.out.println("servicio="+servicio);
      
      String fechaTransaccion = palabra2.substring(48,56);
      System.out.println("fechaTransaccion="+fechaTransaccion);
      
      String oficinaCPT = palabra2.substring(56,64);
      System.out.println("OficinaCPT="+oficinaCPT);
      
      String codigContrapartida = palabra2.substring(64,84);
      System.out.println("codigContrapartida="+codigContrapartida);
      
      String nombre = palabra2.substring(84,144);
      System.out.println("nombre="+nombre);
      
      String valorCobro = palabra2.substring(144,159);
      System.out.println("valorCobro="+valorCobro);
      
      String ordenBanco = palabra2.substring(159,167);
      System.out.println("ordenBanco="+ordenBanco);
      
      String secuencial = palabra2.substring(167,174);
      System.out.println("secuencial="+secuencial);*/
	String respuesta = 	 stripAccents("La cig¸eÒa tocaba el saxofÛn detr·s del palenque de paja.");
	
	System.out.println("Respuesta :"+respuesta);
      
	}
	
	
	
public static String stripAccents(String str) {
if (str == null) {
    return null;
}
char[] array = str.toCharArray();
for (int index = 0; index < array.length; index++) {
    int pos = ORIGINAL.indexOf(array[index]);
    if (pos > -1) {
        array[index] = REPLACEMENT.charAt(pos);
    }
}
return new String(array);
}

}
