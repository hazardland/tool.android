lib.db
================

Android SQLite Codefirst Entity Framework with Multi Thread Support

About
-----------
Framework gives you ability to define class and save load query its objects without precreating any repository or any sqlite table.


Requirements
------------

1. just include library

Usage
-----
We define class

	public class User extends Entity
	{
		public String login;
		public String password;
		public User ()
		{
		
		}
	}

	public class Apple extends Entity
	{
		public String color;
		public User user;
		public Apple ()
		{
		
		}
	}

We define database:

	public Database database;

We init database:

	database = new Database(getBaseContext(), "hazardland.apples", 1);

We register tables:

	database.add (User.class, new Table<User>(database, User.class));
	database.add (Apple.class, new Table<Apple>(database, Apple.class));

We define users:

	User user1 = new User ();
	user1.login = 'Steve_Jobs';
	
	User user2 = new User ();
	user2.login = 'Bill_Gates';

We define apples:

	Apple green_apple = new Apple ();
	green_apple.color = "green";
	green_apple.user = user1;

We save apples:

	database.tables(Apple.class).save (green_apple);

We save users:

	database.tables(User.class).save (user1);
	database.tables(User.class).save (user2);

Than night falls and users fall a sleep...

Next morning:

We load users:

	ArrayList <User> users;
	users.addAll(database.table(User.class).load());

We get user by id:
  
	User bill = database.table(User.class).load(1);

We load users's apples:

	ArrayList <Apple> bills_apples = database.table(Apple.class).of(bill);

We load users's in order, with limit or with custom sql query: using .load (new Query(database.table(Apple.class)))


And much more....
  
