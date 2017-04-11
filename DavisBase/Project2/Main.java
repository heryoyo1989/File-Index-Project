import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.SimpleFormatter;


public class Main {
	static String prompt = "yoyosql> ";
	
	static String widgetTableFileName = "widgets.dat";
	static String tableIdIndexName = "widgets.id.ndx";
	static int id;
	static String name;
	static short quantity;
	static float probability;
	
	static String schemataFileName = "infomation_schema.schemata.ndx";
	static String tablesFileName = "infomation_schema.tables.ndx";
	static String columnsFileName = "infomation_schema.columns.ndx";
	
	static String activeSchema ="infomation_schema";
	
	public static void main(String[] args) throws IOException{
		splashScreen();
		
		
		//System.out.print(Long.parseLong("2012-2-2"));
		//hardCodedCreateTableWithIndex();
		//create_info_table();
		//drop_table("drop table tables;");
		
		//showTables();
		
		/*RandomAccessFile testFile = new RandomAccessFile("tables.ndx", "rw");
		 * 
		//testFile.setLength(5);
		int length=5;
		byte[] a = new byte[length];
		
		testFile.read(a,0,length);
		
		System.out.println(a[length-1]);*/
		
		
		//insert("insert into story(chapter,title,name);");
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userCommand; 
		
		do {  // do-while !exit
			System.out.print(prompt);
			userCommand = scanner.next().trim();

			/*
			 *  This switch handles a very small list of commands of known syntax.
			 *  You will probably want to write a parse(userCommand) method to
			 *  to interpret more complex commands. 
			 */
			if(userCommand.equals("help")){
				help();
			}else if(userCommand.indexOf("create schema ")==0){
				try {
					create_db(userCommand);
				} catch (FileNotFoundException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}else if(userCommand.indexOf("create table")==0){
				
					create_table(userCommand);
				 
			}else if(userCommand.indexOf("use ")==0){
				
					use_schema(userCommand);
				 
			}else if(userCommand.equals("show tables")){
				
					showTables();
				 
			}else if(userCommand.indexOf("insert into ")==0){
				
					insert(userCommand);
				 
			}else if(userCommand.indexOf("drop table ")==0){
				
					drop_table(userCommand);
				 
			}else if(userCommand.indexOf("select * from ")==0){
				
					select(userCommand);
					
			}else if(userCommand.equals("show schemas")){
				
					try {
						showSchemas();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				 
			}else if(!userCommand.equals("exit")){
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
			}
		} while(!userCommand.equals("exit"));
		System.out.println("Exiting...");
	}
	
	private static void create_db(String userCommand) throws FileNotFoundException{
		// TODO 自动生成的方法存根
		// database name
		String schemaName=userCommand.substring(14);
		//no blank
		schemaName=schemaName.trim();
		if(schemaName.indexOf(" ")!=-1){
			System.out.println("Database name is not allowed");
			return;
		}
		
		//write to info_table
		try {
			//@SuppressWarnings("resource")
		    RandomAccessFile schemaFile = new RandomAccessFile(schemataFileName, "rw");
			long location = schemaFile.length();
			schemaFile.seek(location);
			schemaFile.writeByte(schemaName.length());
			schemaFile.writeBytes(schemaName);
			System.out.println("Database created");
			setRowCount("schemata",getRowCount("schemata")+1);
			activeSchema=schemaName;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	
	}
	
	private static void use_schema(String cmd) throws IOException{
		//get name
		String schemaName=cmd.substring(4);
		//no blank
		schemaName=schemaName.trim();
		RandomAccessFile schemaFile;
		Boolean inDatabase=false;
		try {
			schemaFile = new RandomAccessFile(schemataFileName, "rw");
			while(schemaFile.getFilePointer()<schemaFile.length()){
				byte length=schemaFile.readByte();
				String output="";
				for(int i=0;i<length;i++){
					output+=(char)schemaFile.readByte();
				}
			    if(schemaName.equals(output)){
			    	System.out.println("Database changed");
			    	activeSchema = schemaName;
			    	inDatabase=true;
			    	break;
			    }
		    }
			if(!inDatabase){
			   System.out.println("There is no database "+schemaName);	
			}
			
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	private static void showSchemas() throws IOException{
		try {
			RandomAccessFile schemaFile = new RandomAccessFile(schemataFileName, "rw");
			System.out.println("+-------+");
			System.out.println("|schema |");
			System.out.println("+-------+");
			schemaFile.seek(0);
			while(schemaFile.getFilePointer()<schemaFile.length()){
				byte length=schemaFile.readByte();
				String output="";
				for(int i=0;i<length;i++){
					output+=(char)schemaFile.readByte();
				}
				System.out.print("|");
				System.out.print(output);
				System.out.print("\t");
				System.out.print("|\n");
			}
			System.out.println("+-------+");
			
			
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	
	private static void addColumn(String table,String content,int position) throws IOException {
		int index;
		int count=0;
		try {
			
			@SuppressWarnings("resource")
			RandomAccessFile columnsFile = new RandomAccessFile(columnsFileName,"rw");
		
			//get location & add from the tail
			long location = columnsFile.length();
			columnsFile.seek(location);
			//write schema name table_schema
			columnsFile.writeByte(activeSchema.length());
			columnsFile.writeBytes(activeSchema);
			//write table name table_name
			columnsFile.writeByte(table.length());
			columnsFile.writeBytes(table);
		
			content=content.trim();
			//get column name
			String colName;
			index=content.indexOf(" ");
			
			colName=content.substring(0,index);
			columnsFile.writeByte(colName.length());
			columnsFile.writeBytes(colName);
			content=content.substring(index+1);
			
			//position
			columnsFile.writeByte(position);
			//get data type
			String dataType;
			
			index=content.indexOf(" ");//-1 or not -1
			String temp = null;
			if(index!=-1){
				temp=content.substring(0,index);
				content=content.substring(index+1);
			}
			if(index==-1){
				temp=content;
				content="";
			}
			
			if(checkType(temp)==true){
				dataType=temp;
				//System.out.println(dataType);
				columnsFile.writeByte(dataType.length());
				columnsFile.writeBytes(dataType);
				
			}
			

			//get isnullabe, maybe no such a attribute
		   
		    if(content.indexOf("NOT NULL")!=-1){
		    		columnsFile.writeByte(2);
					columnsFile.writeBytes("NO");
		    	}
		    
		    if(content.indexOf("NOT NULL")==-1){
		    	columnsFile.writeByte(3);
				columnsFile.writeBytes("YES");
		    }
		    
		    //column key ?????
		  //columnkey 暂时是no
		   
		    if(content.indexOf("primary key")!=-1){
		    		columnsFile.writeByte(3);
					columnsFile.writeBytes("pri");
		    }
		    
		    if(content.indexOf("primary key")==-1){
		    	columnsFile.writeByte(3);
				columnsFile.writeBytes("   ");
		    }
		
		
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		/*SELECT * FROM COLUMNS;
		+------------------+---------------------+------+-----+
		| Field | Type | Null | Key |
		+------------------+---------------------+------+-----+
		| TABLE_SCHEMA | varchar(64) | NO | |
		| TABLE_NAME | varchar(64) | NO | |
		| TABLE_TYPE | varchar(64) | NO | |
		| ORDINAL_POSITION | int unsigned | NO | |
		| IS_NULLABLE | varchar(3) | NO | |
		| DATA_TYPE | varchar(64) | NO | |
		| COLUMN_TYPE | longtext | NO | |
		| COLUMN_KEY | varchar(3) | NO | |
		+------------------+---------------------+------+-----+*/
		setRowCount("columns",getRowCount("columns")+1);
	}
	
	@SuppressWarnings("unused")
	private static Boolean checkType(String type){
		Boolean typeIsRight=false;
		
		if(type.equals("byte")||
		   type.equals("int")||
		   type.equals("short")||
		   type.equals("long")||
		   type.equals("float")||
		   type.equals("double")||
		   type.equals("datetime")||
		   type.equals("date")){
			typeIsRight=true;
		}
		
		int index1=type.indexOf("(");
		int index2=type.lastIndexOf(")");

		if(index1!=-1&&index2!=-1&&index1<index2){
			String temp=type.substring(0, index1);
			if(temp.equals("char")||temp.equals("varchar")){
				
				typeIsRight=true;
			}
		}
		
		return typeIsRight;
		
	}
	
	@SuppressWarnings("deprecation")
	private static void create_table(String cmd) throws IOException{
		if(activeSchema=="infomation_schema"){
			System.out.println("It's not allowed to create tables in infomation_schema");
			return;
		}
		
		//UNIQUE
		
		
		//get table name
		int index1=cmd.indexOf("(");
		int index2=cmd.lastIndexOf(")");
		
		String tableName=cmd.substring(13,index1);
		
		if(checkTable(tableName)){
			System.out.println("Table is already in schema!");
			return;
		}
		//create the file
		if(tableName!="schemata"&&tableName!="tables"&&tableName!="columns"){
			RandomAccessFile newTableFile = new RandomAccessFile(activeSchema+"."+tableName+".ndx", "rw");
			newTableFile.close();
		}
		RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
		
		//get location & add from the tail
		long location = tableFile.length();
		tableFile.seek(location);
		
		
		//write schema name table_schema
		tableFile.writeByte(activeSchema.length());
		tableFile.writeBytes(activeSchema);
		
		//write table name table_name
		tableFile.writeByte(tableName.length());
		tableFile.writeBytes(tableName);
		

		//table type, no need to implement
		

		//table rows： null at first
		tableFile.writeLong(0);
		
		
		//create date//update date
		/*SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		
		tableFile.writeBytes(df.format(new Date()));
		
		tableFile.writeBytes("00000000");*/
		
		String content=cmd.substring(index1+1,index2);
		
		int index3;
		int position=1;
		while(content.indexOf(",")!=-1){
			index3=content.indexOf(",");
			
			String temp=content.substring(0,index3);
			addColumn(tableName,temp,position);
			position++;
			content=content.substring(index3+1);
		}
		addColumn(tableName,content,position);
		
		System.out.println("Table created successfully!");
		setRowCount("tables",getRowCount("tables")+1);
		//column_name1 data_type(size) [primary key|not null]
		//SELECT * FROM TABLES;
		/*+-----------------+---------------------+------+-----+
		| Field | Type | Null | Key |
		+-----------------+---------------------+------+-----+
		| TABLE_SCHEMA | varchar(64) | NO | |
		| TABLE_NAME | varchar(64) | NO | |
		
		| TABLE_ROWS | long unsigned | YES | |
		
		+-----------------+---------------------+------+-----+
		*/
	}
	
	private static void showTables() throws IOException{
		//get the active 
		RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
		// all the table
		System.out.println("+---------------+");
		System.out.println("|  tableName    |");
		System.out.println("+---------------+");
		while(tableFile.getFilePointer()<tableFile.length()){
			
			byte length1=tableFile.readByte();
			
			
			String schemaName="";
			for(int i=0;i<length1;i++){
				schemaName+=(char)tableFile.readByte();			
			}
			//System.out.println("schema is "+schemaName);

			byte length2=tableFile.readByte();
			
			
			String tableName="";
			for(int j=0;j<length2;j++){
				tableName+=(char)tableFile.readByte();
			}
			if(schemaName.equals(activeSchema))
			System.out.println("|  "+tableName+"\t|");
			
			long rowCount=0;
			rowCount=tableFile.readLong();
			//System.out.println("rowCount is "+rowCount);
		}
		System.out.println("+---------------+");
	}
	
	
	
	private static void drop_table(String cmd) throws IOException{
		
		//tableName
		//drop table
		String table=cmd.substring(11);
		System.out.println(table);
		//check if tableName is in 
		
		if(!checkTable(table)){
			System.out.println("Table is not in schema!");
			return;
		}
		
		File f =new File(table+".ndx");
		//System.out.println(f.delete());
		
		//delete from tables Table
		@SuppressWarnings("resource")
		RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
		long Head = 0;
		long Tail = 0;
		long First;
		String lastTable="";
		while(tableFile.getFilePointer()<tableFile.length()){
			First=tableFile.getFilePointer();
			//schema
			byte length1=tableFile.readByte();
			
			String schemaName="";
			for(int i=0;i<length1;i++){
				schemaName+=(char)tableFile.readByte();			
			}
			
			//table
			byte length2=tableFile.readByte();
			
			String tableName="";
			for(int j=0;j<length2;j++){
				tableName+=(char)tableFile.readByte();
			}
			
			//rowCount;
			tableFile.readLong();
			
			//first become the table to delete----get the head
			if(tableName.equals(table)==true&&lastTable.equals(table)==false){
				Head=First;
			}
			//first become not the table to delete ---- get the tail
			if(tableName.equals(table)==false&&lastTable.equals(table)==true){
				Tail=First;
			}
			
			lastTable=tableName;
		}
		
		//System.out.println(Head);
		//System.out.println(Tail);
		if(Tail==0)Tail=tableFile.length();
		//use head and tail to copy
		int copyLength=(int)(tableFile.length()-Tail);
		//System.out.println(copyLength);
		
		//copy the remaining
		byte[] ToCopy=new byte[copyLength];
		tableFile.seek(Tail);
		tableFile.read(ToCopy, 0, copyLength);
		//System.out.println(ToCopy);
		tableFile.seek(Head);
		tableFile.write(ToCopy, 0, copyLength);
		//set length  head+length-tail  
		tableFile.setLength(Head+copyLength);

		
		
		/********delete from columns Table******************/
		
		RandomAccessFile columnsFile = new RandomAccessFile(columnsFileName, "rw");
		
		
		while(columnsFile.getFilePointer()<columnsFile.length()){
			First=columnsFile.getFilePointer();
			//schema
			byte length1=columnsFile.readByte();
			
			String schemaName="";
			for(int i=0;i<length1;i++){
				schemaName+=(char)columnsFile.readByte();			
			}
			
			//table
			byte length2=columnsFile.readByte();
			
			String tableName="";
			for(int j=0;j<length2;j++){
				tableName+=(char)columnsFile.readByte();
			}
			
			//column
			byte length=columnsFile.readByte();
			
			for(int i=0;i<length;i++){
				columnsFile.readByte();
			}
			
			//position
			byte position=columnsFile.readByte();
			
			//type
			length=columnsFile.readByte();
			String type="";
			for(int i=0;i<length;i++){
				type+=(char)columnsFile.readByte();
			}
			
			//nullable
			length=columnsFile.readByte();
			String nullable="";
			for(int i=0;i<length;i++){
				nullable+=(char)columnsFile.readByte();
			}
			//columnKey
			length=columnsFile.readByte();
			for(int i=0;i<length;i++){
				columnsFile.readByte();
			}
			
			//first become the table to delete----get the head
			if(tableName.equals(table)==true&&lastTable.equals(table)==false){
				Head=First;
			}
			//first become not the table to delete ---- get the tail
			if(tableName.equals(table)==false&&lastTable.equals(table)==true){
				Tail=First;
			}
			if(Tail==0)Tail=columnsFile.length();
			lastTable=tableName;
		}
		
		//System.out.println(Head);
		//System.out.println(Tail);
		//use head and tail to copy
		copyLength=(int)(columnsFile.length()-Tail);
		//System.out.println(copyLength);
		
		//copy the remaining
		ToCopy=new byte[copyLength];
		columnsFile.seek(Tail);
		columnsFile.read(ToCopy, 0, copyLength);
		//System.out.println(ToCopy);
		columnsFile.seek(Head);
		columnsFile.write(ToCopy, 0, copyLength);
		//set length  head+length-tail  
		columnsFile.setLength(Head+copyLength);
		
		
		//delete the file
		//tableFile.close();
		
		
	}
	
	private static void create_info_table() throws IOException{
		try {
			//RandomAccessFile schemaFile = new RandomAccessFile(schemataFileName, "rw");
			//RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
			//RandomAccessFile columnFile = new RandomAccessFile(columnsFileName, "rw");
			
			//add schema
			create_db("create schema infomation_schema");
			//add three tables
			create_table("create table schemata(SCHEMA_NAME VARCHAR(64) NOT NULL)");
			create_table("create table tables(TABLE_SCHEMA VARCHAR(64) NOT NULL,TABLE_NAME VARCHAR(64) NOT NULL,TABLE_ROWS LONG)");
			create_table("create table columns(TABLE_SCHEMA VARCHAR(64) NOT NULL,TABLE_NAME VARCHAR(64) NOT NULL,COLUMN_NAME VARCHAR(64) NOT NULL,ORDINAL_POSITION INT NOT NULL,COLUMN_TYPE VARCHAR(64) NOT NULL,IS_NULLABLE VARCHAR(3) NOT NULL,COLUNM_KEY VARCHAR(3) NOT NULL)");
			
			
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		
		/*SELECT * FROM COLUMNS;
		+--------------------+-------------+------------------+------------------+-------------+-------------+------------+
		| TABLE_SCHEMA | TABLE_NAME | COLUMN NAME | ORDINAL_POSITION | COLUMN_TYPE | IS_NULLABLE | COLUMN_KEY |
		+--------------------+-------------+------------------+------------------+-------------+-------------+------------+
		| information_schema | SCHEMATA | SCHEMA_NAME | 1 | varchar(64) | NO | |
		| information_schema | TABLES | TABLE_SCHEMA | 1 | varchar(64) | NO | |
		| information_schema | TABLES | TABLE_NAME | 2 | varchar(64) | NO | |
		| information_schema | TABLES | TABLE_ROWS | 3 | long int | NO | |
		| information_schema | COLUMNS | TABLE_SCHEMA | 1 | varchar(64) | NO | |
		| information_schema | COLUMNS | TABLE_NAME | 2 | varchar(64) | NO | |
		| information_schema | COLUMNS | COLUMN_NAME | 3 | varchar(64) | NO | |
		| information_schema | COLUMNS | ORDINAL_POSITION | 4 | int | NO | |
		| information_schema | COLUMNS | COLUMN_TYPE | 5 | varchar(64) | NO | |
		| information_schema | COLUMNS | IS_NULLABLE | 6 | varchar(3) | NO | |
		| information_schema | COLUMNS | COLUMN_KEY | 7 | varchar(3) | NO | |
		+--------------------+-------------+------------------+------------------+-------------+-------------+------------+*/
	}

	private static long getRowCount(String tableName) throws IOException{
		long rowCount=0;
		RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
		while(tableFile.getFilePointer()<tableFile.length()){
			byte length=0;
			//schecma
			length=tableFile.readByte();
			String schema="";
			for(int i=0;i<length;i++){
				schema+=(char)tableFile.readByte();
			}
			
			//table
			length=tableFile.readByte();
			String table="";
			for(int i=0;i<length;i++){
				table+=(char)tableFile.readByte();
			}
			if(table.equals(tableName))rowCount=tableFile.readLong();
			if(!table.equals(tableName))tableFile.readLong();
		}
		
		return rowCount;
	}
	private static void setRowCount(String tableName,long rowCount) throws IOException{
		RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
		while(tableFile.getFilePointer()<tableFile.length()){
			byte length=0;
			//schecma
			length=tableFile.readByte();
			String schema="";
			for(int i=0;i<length;i++){
				schema+=(char)tableFile.readByte();
			}
			
			//table
			length=tableFile.readByte();
			String table="";
			for(int i=0;i<length;i++){
				table+=(char)tableFile.readByte();
			}
			if(table.equals(tableName))tableFile.writeLong(rowCount);;
			if(!table.equals(tableName))tableFile.readLong();
		}
	}
	private static Boolean checkTable(String tableName) throws IOException{
		Boolean IsIn=false;
		RandomAccessFile tableFile = new RandomAccessFile(tablesFileName, "rw");
		while(tableFile.getFilePointer()<tableFile.length()){
			byte length=0;
			//schecma
			length=tableFile.readByte();
			String schema="";
			for(int i=0;i<length;i++){
				schema+=(char)tableFile.readByte();
			}
			
			//table
			length=tableFile.readByte();
			String table="";
			for(int i=0;i<length;i++){
				table+=(char)tableFile.readByte();
			}
			if(schema.equals(activeSchema)&&table.equals(tableName)){
				return true;
			}
			//row count
			tableFile.readLong();
		}
		return IsIn;
	}
	
	@SuppressWarnings("unchecked")
	private static void insert(String cmd) throws IOException{
		if(activeSchema=="infomation_schema"){
			System.out.println("Yon don't select any schemas!");
			
			return;
		}
		
		int index1=cmd.indexOf("(");
		int index2=cmd.lastIndexOf(")");
		int index3=cmd.indexOf("values");
		if(index1==-1||index2==-1||index3==-1){
			System.out.println("Input format is wrong.");
		}
	/*********************get types********************************/		
		String tableName=cmd.substring(12,index3-1);
		
		ArrayList typeList=new ArrayList();
		
		//get table name insert into//
		RandomAccessFile columnsFile = new RandomAccessFile(columnsFileName,"rw");
		
		while(columnsFile.getFilePointer()<columnsFile.length()){
			//schema
			byte length=columnsFile.readByte();
			String schema="";
			for(int i=0;i<length;i++){
				schema+=(char)columnsFile.readByte();
			}
			//System.out.println(schema);
			//table
			length=columnsFile.readByte();
			String table="";
			for(int i=0;i<length;i++){
				table+=(char)columnsFile.readByte();
			}
			//column
			length=columnsFile.readByte();
			
			for(int i=0;i<length;i++){
				columnsFile.readByte();
			}
			
			//position
			byte position=columnsFile.readByte();
			
			//type
			length=columnsFile.readByte();
			String type="";
			for(int i=0;i<length;i++){
				type+=(char)columnsFile.readByte();
			}
			if(schema.equals(activeSchema)&&table.equals(tableName)){
				//System.out.println(type);
				//Types
				typeList.add(type);
			}
			
			//nullable
			length=columnsFile.readByte();
			String nullable="";
			for(int i=0;i<length;i++){
				nullable+=(char)columnsFile.readByte();
			}
			//columnkey
			length=columnsFile.readByte();
			String key="";
			for(int i=0;i<length;i++){
				key+=(char)columnsFile.readByte();
			}
			//System.out.println(nullable);
		}
		/*for(int i=0;i<typeList.size();i++){
			System.out.println(typeList.get(i));
		}*/
		/*********************get types ends********************************/
		///////////////insert
		
		//number of attributes参数数量啊！！！
		
		//check whether tableName is in Tables table and the active schema
		if(!checkTable(tableName)){
			System.out.println("Table is not in schema!");
			return;
		}
		
		//get columns column type in columns table
		RandomAccessFile insertFile = new RandomAccessFile(activeSchema+"."+tableName+".ndx","rw");
		String content=cmd.substring(index1+1,index2);
		long index = insertFile.length();
		insertFile.seek(index);
		
		int position=0;
		StringTokenizer tokens=new StringTokenizer(content,",");
		if(tokens.countTokens()!=typeList.size()){
			System.out.println("Insert denied");
			return;
		}
		while(tokens.hasMoreTokens()){
			String temp=tokens.nextToken();
			//get type and different type,diff write
			String typeCheck=(String)typeList.get(position);
			//add values to existed table in active schema
			if(typeCheck.indexOf("varchar")!=-1||typeCheck.indexOf("char")!=-1){
				if(temp.indexOf("'")!=-1){
					temp=temp.substring(1,temp.length()-1);
				}
				temp=temp.trim();
				insertFile.writeByte(temp.length());
				insertFile.writeBytes(temp);
			}
			if(typeCheck.equals("byte")){
				insertFile.writeByte(Byte.parseByte(temp));
			}
			if(typeCheck.equals("short")){
				insertFile.writeShort(Short.parseShort(temp));
			}
			if(typeCheck.equals("int")){
				insertFile.writeInt(Integer.parseInt(temp));
			}
			if(typeCheck.equals("long")){
				insertFile.writeLong(Long.parseLong(temp));
			}
			if(typeCheck.equals("float")){
				insertFile.writeFloat(Float.parseFloat(temp));
			}
			if(typeCheck.equals("double")){
				insertFile.writeDouble(Double.parseDouble(temp));
			}
			//date format
			if(typeCheck.equals("date")){
				//insertFile.writeByte(temp.length());
				//insertFile.writeBytes(temp);
				if(temp.indexOf("'")!=-1){
					temp=temp.substring(1,temp.length()-1);
				}
				if(temp.indexOf(",")!=-1){
					System.out.println("Date format is wrong!");
					insertFile.setLength(index);
					return;
				}
				temp=temp.trim();
				if(temp.indexOf("-")!=-1){
					int indexO=temp.indexOf("-");
					int indexT=temp.lastIndexOf("-");
					String tempStr=temp.substring(0,indexO)+temp.substring(indexO+1,indexT)+temp.substring(indexT+1);
					insertFile.writeLong(Long.parseLong(tempStr));
				}
				if(temp.indexOf("-")==-1){
					insertFile.writeLong(Long.parseLong(temp));
				}
				
			}
			if(typeCheck.equals("datatime")){
				//insertFile.writeByte(temp.length());
				//insertFile.writeBytes(temp);
				insertFile.writeLong(Long.parseLong(temp));
			}
			position++;
		}
		
		//error
		
		//success infomation
		System.out.println("Query is OK!");
		//addrow
		setRowCount(tableName,getRowCount(tableName)+1);
		//deal with null or key
		insertFile.close();
		//attribute is not enough
		
	}
	
	private static void select(String cmd) throws IOException {
		//get table name
		String leftStr=cmd.substring("select * from ".length());
		 
		//just the table
		if(leftStr.indexOf("where")==-1){
			String table=leftStr;
			if(!checkTable(table)){
				System.out.println("Table"+table+ "is not in schema!");
				return;
			}
			showAll(table);
		}
		//get where and table
		if(leftStr.indexOf("where")!=-1){
			int index1=leftStr.indexOf(" ");
			int index2=leftStr.lastIndexOf(" ");
			String table=leftStr.substring(0, index1);
			if(!checkTable(table)){
				System.out.println("Table is not in schema!");
				return;
			}
			String condition=leftStr.substring(index2+1,leftStr.length());
			
			if(condition.indexOf("=")!=-1){
				int index3=condition.indexOf("=");
				String item=condition.substring(0,index3);
				String data=condition.substring(index3+1,condition.length());
				showSpecific(table,item,data,"=");
			}
			if(condition.indexOf(">")!=-1){
				int index3=condition.indexOf(">");
				String item=condition.substring(0,index3);
				String data=condition.substring(index3+1,condition.length());
				showSpecific(table,item,data,">");
			}
			if(condition.indexOf("<")!=-1){
				int index3=condition.indexOf("<");
				String item=condition.substring(0,index3);
				String data=condition.substring(index3+1,condition.length());
				showSpecific(table,item,data,"<");
			}
		}
	}
	
	private static void showAll(String table) throws IOException{
		//typeList
		ArrayList typeList=new ArrayList();
		ArrayList nameList=new ArrayList();
		
		RandomAccessFile columnsFile = new RandomAccessFile(columnsFileName,"rw");
		
		while(columnsFile.getFilePointer()<columnsFile.length()){
			//schema
			byte length=columnsFile.readByte();
			String schema="";
			for(int i=0;i<length;i++){
				schema+=(char)columnsFile.readByte();
			}
			//System.out.println(schema);
			//table
			length=columnsFile.readByte();
			String tableName="";
			for(int i=0;i<length;i++){
				tableName+=(char)columnsFile.readByte();
			}
			//column
			length=columnsFile.readByte();
			String columns="";
			for(int i=0;i<length;i++){
				columns+=(char)columnsFile.readByte();
			}
			
			//position
			byte position=columnsFile.readByte();
			
			//type
			length=columnsFile.readByte();
			String type="";
			for(int i=0;i<length;i++){
				type+=(char)columnsFile.readByte();
			}
			if(schema.equals(activeSchema)&&tableName.equals(table)){
				//System.out.println(type);
				//Types
				typeList.add(type);
				nameList.add(columns);
			}
			
			//nullable
			length=columnsFile.readByte();
			String nullable="";
			for(int i=0;i<length;i++){
				nullable+=(char)columnsFile.readByte();
			}
			//columnkey
			length=columnsFile.readByte();
			String key="";
			for(int i=0;i<length;i++){
				key+=(char)columnsFile.readByte();
			}
			//System.out.println(nullable);
		}
		
		for(int i=0;i<nameList.size();i++){
			System.out.print("| "+nameList.get(i)+"\t");
		}
		System.out.println("|");
		
		//show file
		RandomAccessFile showFile=new RandomAccessFile(activeSchema+"."+table+".ndx","rw");
		while(showFile.getFilePointer()<showFile.length()){
			for(int i=0;i<typeList.size();i++){
				String typeCheck=(String)typeList.get(i);
				//need to add column key
				/*if(table.equals("columns")&&i==typeList.size()-1){
					System.out.print("\t|");
					continue;
				}*/
				byte length;
				int j;
				
				if(typeCheck.indexOf("varchar")!=-1||typeCheck.indexOf("char")!=-1){
					length=showFile.readByte();
					String rst="";
					for(j=0;j<length;j++){
						rst+=(char)showFile.readByte();
					}
					System.out.print("| "+rst+"\t");
				}
				if(typeCheck.equals("byte")){
					byte rst=showFile.readByte();
					System.out.print("| "+rst+"\t");
				}
				if(typeCheck.equals("short")){
					short rst=showFile.readShort();
					System.out.print("| "+rst+"\t");
				}
				if(typeCheck.equals("int")){
					int rst=showFile.readInt();
					System.out.print("| "+rst+"\t");
				}
				if(typeCheck.equals("long")){
					long rst=showFile.readLong();
					System.out.print("| "+rst+"\t");
				}
				if(typeCheck.equals("float")){
					float rst=showFile.readFloat();
					System.out.print("| "+rst+"\t");
				}
				if(typeCheck.equals("double")){
					double rst=showFile.readDouble();
					System.out.print("| "+rst+"\t");
				}
				//date format
				if(typeCheck.equals("date")){
					/*length=showFile.readByte();
					String rst="";
					for(j=0;j<length;j++){
						rst+=(char)showFile.readByte();
					}*/
					long rst1=showFile.readLong();
					String rst2=rst1+"";
					String rst3=rst2.substring(0, 4)+"-"+rst2.substring(4, 6)+"-"+rst2.substring(6, 8);
					System.out.print("| "+rst3+"\t");
				}
				if(typeCheck.equals("datetime")){
					/*length=showFile.readByte();
					String rst="";
					for(j=0;j<length;j++){
						rst+=(char)showFile.readByte();
					}*/
					long rst1=showFile.readLong();
					String rst2=rst1+"";
					String rst3=rst2.substring(0, 4)+"-"+rst2.substring(4, 6)+"-"+rst2.substring(6, 8)+" "+rst2.substring(8, 10)+":"+rst2.substring(10, 12)+":"+rst2.substring(12, 14);
					System.out.print("| "+rst3+"\t");
					
				}
			}
			System.out.println("|");
		}
	}
	
	private static void showSpecific(String table,String item,String data,String relation) throws IOException{
		//typeList
				ArrayList typeList=new ArrayList();
				ArrayList nameList=new ArrayList();
				
				RandomAccessFile columnsFile = new RandomAccessFile(columnsFileName,"rw");
				
				while(columnsFile.getFilePointer()<columnsFile.length()){
					//schema
					byte length=columnsFile.readByte();
					String schema="";
					for(int i=0;i<length;i++){
						schema+=(char)columnsFile.readByte();
					}
					//System.out.println(schema);
					//table
					length=columnsFile.readByte();
					String tableName="";
					for(int i=0;i<length;i++){
						tableName+=(char)columnsFile.readByte();
					}
					//column
					length=columnsFile.readByte();
					String columns="";
					for(int i=0;i<length;i++){
						columns+=(char)columnsFile.readByte();
					}
					
					//position
					byte position=columnsFile.readByte();
					
					//type
					length=columnsFile.readByte();
					String type="";
					for(int i=0;i<length;i++){
						type+=(char)columnsFile.readByte();
					}
					if(schema.equals(activeSchema)&&tableName.equals(table)){
						//System.out.println(type);
						//Types
						typeList.add(type);
						nameList.add(columns);
					}
					
					//nullable
					length=columnsFile.readByte();
					String nullable="";
					for(int i=0;i<length;i++){
						nullable+=(char)columnsFile.readByte();
					}
					//columnkey
					length=columnsFile.readByte();
					String key="";
					for(int i=0;i<length;i++){
						key+=(char)columnsFile.readByte();
					}
					//System.out.println(nullable);
				}
				int position=0;
				for(int i=0;i<nameList.size();i++){
					System.out.print("| "+nameList.get(i)+"\t");
					if(item.equals((String)nameList.get(i))){
						position=i;
					}
				}
				
				System.out.println("|");
				
				//ORDINAL_POSITION=1
				//show file
				RandomAccessFile showFile=new RandomAccessFile(activeSchema+"."+table+".ndx","rw");
				while(showFile.getFilePointer()<showFile.length()){
					boolean showBool=false;
					String output="";
					for(int i=0;i<typeList.size();i++){
						
						String typeCheck=(String)typeList.get(i);
						//need to add column key
						/*if(table.equals("columns")&&i==typeList.size()-1){
							output+="\t|";
							continue;
						}*/
						byte length;
						int j;
						if(typeCheck.indexOf("(")!=-1){
							int index=typeCheck.indexOf("(");
							typeCheck=typeCheck.substring(0,index);
						}
						
						/*switch(typeCheck){
						case "VARCHAR":
							length=showFile.readByte();
							String rst="";
							for(j=0;j<length;j++){
								rst+=(char)showFile.readByte();
							}
							output+="| "+rst+"\t";
							if(rst.equals(data)&&i==position){
								showBool=true;
							}
							break;
						case "CHAR":
							length=showFile.readByte();
							rst="";
							for(j=0;j<length;j++){
								rst+=(char)showFile.readByte();
							}
							if(rst.equals(data)&&i==position){
								showBool=true;
								output+="| "+rst+"\t";
							}
							break;
						case "BYTE":
							byte rst1=showFile.readByte();
							
							if(rst1==Byte.parseByte(data)&&i==position)showBool=true;
							output+="| "+rst1+"\t";
							break;
						case "SHORT":
							break;
						case "INT":
							break;
						case "LONG":
							break;
						case "DOUBLE":
							break;
						case "FLOAT":
							break;
						case "DATETIME":
							break;
						case "DATE":
							break;
						}*/
						
						
						if(typeCheck.indexOf("varchar")!=-1||typeCheck.indexOf("char")!=-1){
							length=showFile.readByte();
							String rst="";
							for(j=0;j<length;j++){
								rst+=(char)showFile.readByte();
							}
							if(i==position){
								if(relation.equals("=")&&rst.equals(data)){
									showBool=true;
								}
							}
							output+="| "+rst+"\t";
						}
						
						//date format
						else if(typeCheck.equals("date")){
							/*length=showFile.readByte();
							String rst="";
							for(j=0;j<length;j++){
								rst+=(char)showFile.readByte();
							}*/
							if(data.indexOf("'")!=-1){
								data=data.substring(1,data.length()-1);
							}
							
							long rst1=showFile.readLong();
							String rst2=rst1+"";
							String rst3=rst2.substring(0, 4)+"-"+rst2.substring(4, 6)+"-"+rst2.substring(6, 8);
							//System.out.print("| "+rst3+"\t");
							if(data.indexOf("-")!=-1){
								int indexO=data.indexOf("-");
								int indexT=data.lastIndexOf("-");
								data=data.substring(0,indexO)+data.substring(indexO+1,indexT)+data.substring(indexT+1);
								//insertFile.writeLong(Long.parseLong(tempStr));
							}
							if(data.indexOf("-")==-1){
								//insertFile.writeLong(Long.parseLong(temp));
							}
							
							if(i==position){
								if(relation.equals("=")&&rst1==Long.parseLong(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst1>Long.parseLong(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst1<Long.parseLong(data)){
									showBool=true;
								}
							}
							output+="| "+rst3+"\t";
						}
						else if(typeCheck.equals("datetime")){
							/*length=showFile.readByte();
							String rst="";
							for(j=0;j<length;j++){
								rst+=(char)showFile.readByte();
							}*/
							
							
							long rst1=showFile.readLong();
							String rst2=rst1+"";
							String rst3=rst2.substring(0, 4)+"-"+rst2.substring(4, 6)+"-"+rst2.substring(6, 8)+" "+rst2.substring(8, 10)+":"+rst2.substring(10, 12)+":"+rst2.substring(12, 14);
							//System.out.print("| "+rst3+"\t");
							if(i==position){
								if(relation.equals("=")&&rst1==Long.parseLong(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst1>Long.parseLong(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst1<Long.parseLong(data)){
									showBool=true;
								}
							}
							output+="| "+rst3+"\t";
						}
						else if(typeCheck.equals("double")){
							double rst=showFile.readDouble();
							if(i==position){
								if(relation.equals("=")&&rst==Double.parseDouble(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst>Double.parseDouble(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst<Double.parseDouble(data)){
									showBool=true;
								}
								
							}
							output+="| "+rst+"\t";
						}
						else if(typeCheck.equals("float")){
							float rst=showFile.readFloat();
							if(i==position){
								if(relation.equals("=")&&rst==Float.parseFloat(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst<Float.parseFloat(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst>Float.parseFloat(data)){
									showBool=true;
								}
							}
							output+="| "+rst+"\t";
						}
						
						
						else if(typeCheck.equals("long")){
							long rst=showFile.readLong();
							if(i==position){
								if(relation.equals("=")&&rst==Long.parseLong(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst>Long.parseLong(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst<Long.parseLong(data)){
									showBool=true;
								}
							}
							output+="| "+rst+"\t";
						}
						else if(typeCheck.equals("int")){
							int rst=showFile.readInt();
							if(i==position){
								if(relation.equals("=")&&rst==Integer.parseInt(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst>Integer.parseInt(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst<Integer.parseInt(data)){
									showBool=true;
								}
							}
							output+="| "+rst+"\t";
						}
						else if(typeCheck.equals("short")){
							short rst=showFile.readShort();
							if(i==position){
								if(relation.equals("=")&&rst==Short.parseShort(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst>Short.parseShort(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst<Short.parseShort(data)){
									showBool=true;
								}
							}
							output+="| "+rst+"\t";
						}
						else if(typeCheck.equals("byte")){
							byte rst=showFile.readByte();
							
							if(i==position){
								if(relation.equals("=")&&rst==Byte.parseByte(data)){
									showBool=true;
								}
								if(relation.equals(">")&&rst>Byte.parseByte(data)){
									showBool=true;
								}
								if(relation.equals("<")&&rst<Byte.parseByte(data)){
									showBool=true;
								}
							}
							output+="| "+rst+"\t";
						}
						
						
						
					}
					if(showBool==true){
						System.out.println(output+"|");
					}
					
				}
	}

	private static void help() {
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("\tcreate schema <schema_name>;   ");
		System.out.println("\tuse <schema_name>;");
		System.out.println("\tshow schemas; ");
		System.out.println("\tshow tables; ");
		System.out.println("\tcreate table <table_name>; ");
		System.out.println("\tdrop table <table_name>; ");
		System.out.println("\tinsert into <table_name> values(<colname1> <colDataType1>,<colname2> <colDataType2>,.....);   Display all records in the table.");
		System.out.println("\tselect * from <table_name> where filter;     Only > < = are supported.");
		
		
		System.out.println("\thelp;          Show this help information");
		System.out.println("\texit;          Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*",80));
	}

	/**
	 *  Display the welcome "splash screen"
	 */
	public static void splashScreen() {
		System.out.println(line("*",80));
	    System.out.println("Welcome to YoYoBaseLite"); // Display the string.
		version();
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}
	
	private static void version() {
		System.out.println("YoYoBaseLite v1.0\n");
	}
	
	private static String line(String s, int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	
	
	public static void hardCodedCreateTableWithIndex() {
		//long recordPointer;
		try {
			@SuppressWarnings("resource")
			RandomAccessFile widgetTableFile = new RandomAccessFile(widgetTableFileName, "rw");
			@SuppressWarnings("resource")
			RandomAccessFile tableIdIndex = new RandomAccessFile(tableIdIndexName, "rw");
			
			id = 1;
			name = "alpha";
			quantity = 847;
			probability = 0.341f;
			
			tableIdIndex.writeInt(id);
			tableIdIndex.writeLong(widgetTableFile.getFilePointer());
			
			widgetTableFile.writeInt(id);
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);
			
			id = 2;
			name = "beta";
			quantity = 1472;
			probability = 0.89f;
			
			tableIdIndex.writeInt(id);
			tableIdIndex.writeLong(widgetTableFile.getFilePointer());
			widgetTableFile.writeInt(id);
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);

			id = 3;
			name = "gamma";
			quantity = 41;
			probability = 0.5f;
			
			tableIdIndex.writeInt(id);
			tableIdIndex.writeLong(widgetTableFile.getFilePointer());
			widgetTableFile.writeInt(id);
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);

			id = 4;
			name = "delta";
			quantity = 4911;
			probability = 0.4142f;
			
			tableIdIndex.writeInt(id);
			tableIdIndex.writeLong(widgetTableFile.getFilePointer());
			widgetTableFile.writeInt(id);
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);

			id = 5;
			name = "epsilon";
			quantity = 6823;
			probability = 0.618f;
			
			tableIdIndex.writeInt(id);
			tableIdIndex.writeLong(widgetTableFile.getFilePointer());
			widgetTableFile.writeInt(id);
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

}
