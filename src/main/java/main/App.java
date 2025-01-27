package main;

import java.util.ArrayList;
import java.util.Scanner;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;

class Player implements java.io.Serializable {
	String name;

	Player(String name_) {
		name = name_;
	}

	Monster.State attack(Monster monster) {
		return monster.takeDamage(10);
	}
}

class Monster implements java.io.Serializable {
	String type;
	int health;

	Monster(String type_, int health_) {
		type = type_;
		health = health_;
	}

	void printInfo(int number) {
		System.out.println(String.format("%d: %s / %sHP", number, type, health));
	}

	enum State {
		Alive,
		Dead,
	}

	State takeDamage(int dmg) {
		health -= dmg;

		if (health > 0) {
			System.out.println(String.format("Hirviöllä on %d elämää jäljellä.", health));

			return State.Alive;
		} else {
			System.out.println(String.format("%s on kuollut!", type));

			return State.Dead;
		}
	}
}

class Cave implements java.io.Serializable {
	Player player;

	ArrayList<Monster> monsters;

	Cave(Player player_) {
		player = player_;

		monsters = new ArrayList<Monster>();
	}

	void addMonster(Monster monster) {
		monsters.add(monster);
	}

	void listMonsters() {
		if (monsters.isEmpty()) {
			System.out.println("Luola on tyhjä.");

			return;
		}

		System.out.println("Luolan hirviöt:");

		for (int i = 0; i < monsters.size(); i ++) {
			monsters.get(i).printInfo(i + 1);
		}
	}
}



/**
* Actual implementation
**/
class Impl {
	Scanner in;
	Cave cave;

	Impl(String[] args) {
		in = new Scanner(System.in);

		System.out.println("Syötä pelaajan nimi:");

		Player player = new Player(in.nextLine());

		cave = new Cave(player);
	}

	void close() {
		in.close();

		System.out.println("Peli päättyy. Kiitos pelaamisesta!");
	}

	void LisaaLuolaanHirvio() {
		System.out.println("Anna hirviön tyyppi:");

		String type = in.nextLine();

		System.out.println("Anna hirviön elämän määrä numerona:");

		int health = Integer.parseInt(in.nextLine());

		Monster monster = new Monster(type, health);

		cave.addMonster(monster);
	}

	void ListaaHirviot() {
		cave.listMonsters();
	}

	void HyokkaaHirvioon() {
		System.out.println("Valitse hirviö, johon hyökätä:");

		cave.listMonsters();

		int number = Integer.parseInt(in.nextLine()) - 1;

		Monster monster = cave.monsters.get(number);

		System.out.println(String.format("%s hyökkää %s hirviöön!", cave.player.name, monster.type));

		Monster.State ms = cave.player.attack(monster);

		switch (ms) {
			case Alive:
				break;

			case Dead:
				cave.monsters.remove(number);

				break;
		}
	}

	void TallennaPeli() {
		System.out.println("Anna tiedoston nimi, johon peli tallentaa:");

		String filename = in.nextLine();

		try {
			var out = new ObjectOutputStream(new FileOutputStream(filename));

			out.writeObject(cave);

			out.close();
		} catch(IOException i){
			i.printStackTrace();
		}

		System.out.println(String.format("Peli tallennettiin tiedostoon %s.", filename));
	}

	void LataaPeli() {
		System.out.println("Anna tiedoston nimi, josta peli ladataan:");

		String filename = in.nextLine();

		try {
			var in = new ObjectInputStream(new FileInputStream(filename));

			cave = (Cave) in.readObject();

			in.close();
		} catch(IOException i){
			i.printStackTrace();
		} catch(ClassNotFoundException i) {
			i.printStackTrace();
		}

		System.out.println(String.format("Peli ladattu tiedostosta %s. Tervetuloa takaisin, %s.", filename, cave.player.name));
	}

	boolean run() {
		System.out.println("1) Lisää luolaan hirviö");
		System.out.println("2) Listaa hirviöt");
		System.out.println("3) Hyökkää hirviöön");
		System.out.println("4) Tallenna peli");
		System.out.println("5) Lataa peli");

		System.out.println("0) Lopeta peli");

		int selection = Integer.parseInt(in.nextLine());

		if (selection == 0) {
			return false;

		} else if (selection == 1) {
			LisaaLuolaanHirvio();

		} else if (selection == 2) {
			ListaaHirviot();

		} else if (selection == 3) {
			HyokkaaHirvioon();

		} else if (selection == 4) {
			TallennaPeli();

		} else if (selection == 5) {
			LataaPeli();

		} else {
			System.out.println("Tuntematon valinta, yritä uudestaan.");
		}

		return true;
	}
}

/**
* Impl runner, do not edit.
*/
public class App {
	public static void main(String[] args) {
		Impl impl = new Impl(args);

		while (impl.run()) {}

		impl.close();
	}
}
