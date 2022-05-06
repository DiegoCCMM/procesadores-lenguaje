/*****************************************************************/
// File:   Attributes.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   enero 2022
//         Clase única para almacenar los atributos que pueden aparecer en el traductor de adac
//         Se hace como clase plana, por simplicidad. Los atributos que se pueden almacenar
//         se autoexplican con el nombre
/*****************************************************************/

package lib.attributes;
import lib.symbolTable.*;

public class Attributes implements Cloneable {
    public Symbol.Types type;
    public Symbol.ParameterClass parClass;

    public int valInt;
    public boolean valBool;
    public char valChar;
    public String valString;
    public int tamanyo_vector;


    public Attributes() {
        type = Symbol.Types.UNDEFINED;
        parClass = Symbol.ParameterClass.NONE;
    }

    //Atributo entero no parametro
    public Attributes(int value){
        type = Symbol.Types.INT;
        valInt = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo entero parametro
    public Attributes(int value, Symbol.ParameterClass parameter){
        type = Symbol.Types.INT;
        valInt = value;
        parClass = parameter;
    }

    //Atributo char no parametro
    public Attributes(char value){
        type = Symbol.Types.CHAR;
        valChar = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo char parametro
    public Attributes(char value, Symbol.ParameterClass parameter){
        type = Symbol.Types.CHAR;
        valChar = value;
        parClass = parameter;
    }

    //Atributo bool no parametro
    public Attributes(boolean value){
        type = Symbol.Types.BOOL;
        valBool = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo bool parametro
    public Attributes(boolean value, Symbol.ParameterClass parameter){
        type = Symbol.Types.BOOL;
        valBool = value;
        parClass = parameter;
    }

    //Atributo string
    public Attributes(String value){
        type = Symbol.Types.STRING;
        valString = value;
        parClass = Symbol.ParameterClass.NONE;
    }

    //Atributo vector no parametro
    public Attributes(int tam, Symbol.Types vector_type){
        type = vector_type;
        tamanyo_vector=tam;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo vector parametro
    public Attributes(int tam, Symbol.Types vector_type,Symbol.ParameterClass parameter){
        type = vector_type;
        tamanyo_vector=tam;
        parClass = parameter;
    }



    public Attributes clone() {
    	try {
    		return (Attributes) super.clone();
    	}
    	catch (CloneNotSupportedException e) {
    		return null; 
    	}
    }

    public String toString() {
        return
            "type = " + type + "\n" +
            "parClass = " + parClass + "\n" ;
            //COMPLETAR
        
    }

    public static void comprobacion_tipo(Attributes atr_1, Attributes atr_2, boolean operador_booleano){
        if(operador_booleano){
            atr_1.comprobacion_tipo(Symbol.Types.BOOL);
            atr_2.comprobacion_tipo(Symbol.Types.BOOL);
        }else{//cualquier otro caso con los operadores aditivos o multiplicativos
            atr_1.comprobacion_tipo(Symbol.Types.INT);
            atr_2.comprobacion_tipo(Symbol.Types.INT);
        }
    }

    /* Comprobracion del tipo de dato|variable|expresion */
    public boolean comprobacion_tipo(Symbol.Types tipo_esperado){
        if(this.type != tipo_esperado){
            //TODO:add datos de linea/columna?
            System.out.println("INCOMPATIBILIDAD DE TIPOS: Se esperaba un dato de tipo "+ tipo_esperado);
            this.type=tipo_esperado;
            return(false);
        }
        return(true);
    }


    /* Comprobacion del tipo en operadores relacionales */
    public  void comprobacion_mismo_tipo(Attributes sim){
        if(this.type != sim.type){
            System.out.println("No se pueden comparar tipos distintos");
        }
    }
}
