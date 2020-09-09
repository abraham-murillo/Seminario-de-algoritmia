#include <iostream>
#include "Lista.cpp" // AÑADIDO EN CASA
#include "Paquete.h"
using namespace std;

int main(){
	/*
	Paquete p1(1, "yo", "tu", 1);
	cout << p1 << '\n';
	Paquete p2;
	cin >> p2;
	cout << p2 << '\n';
	*/
	cout << "Examen diagnostico\n";
	char salir;
	int op;

	/*
	Lista<int> lista;
	lista.insertarInicio(5);
	lista.insertarInicio(6);
	lista.insertarInicio(7);
	lista.mostrar();
	lista.eliminarInicio();
	lista.mostrar();
	*/
	
	Lista<Paquete> lis;
	do{
		cout << "Ingrese la opcion a realizar\n";
		cout << "1. Agregar paquete\n";
		cout << "2. Eliminar paquete\n";
		cout << "3. Mostrar\n";
		cin >> op;
		switch(op){
			case 1:{
				Paquete p;
				cin >> p;
				lis.insertarInicio(p);
				break;
			}
			case 2:{
				lis.eliminarInicio();
				break;
			}
			case 3:{
				lis.mostrar();
				break;
			}
		}
		cout << "Para salir presione s, de lo contrario otra tecla\n";
		cin >> salir;
	}while( !(salir == 's' || salir == 'S') );
	return 0;
}
