SET TERM ^ ;
CREATE OR ALTER PACKAGE  DBMS_RANDOM
AS
BEGIN
	PROCEDURE INITIALIZE(p_seed INT);
	PROCEDURE TERMINATE();
	PROCEDURE SEED(p_seed VARCHAR);
	
	FUNCTION RANDOM ()  RETURNS INT;

	FUNCTION NORMAL() RETURNS  DECFLOAT;

	FUNCTION RAND_STRING (
		p_otp CHAR,
		p_len INT
	) RETURNS VARCHAR;

	
	FUNCTION RAND_VALUE (
		p_low DECFLOAT DEFAULT -1,
		p_high DECFLOAT DEFAULT 1
	)  RETURNS  DECFLOAT;
END^
RECREATE PACKAGE BODY DBMS_RANDOM
AS
BEGIN
	PROCEDURE INITIALIZE(p_seed INT)
	EXTERNAL NAME 'ru.example.DBMS_Random.initialize(int)'
  	ENGINE JAVA;

	PROCEDURE TERMINATE()
	EXTERNAL NAME 'ru.example.DBMS_Random.terminate()'
  	ENGINE JAVA;
  	
	PROCEDURE SEED(p_seed VARCHAR)
	EXTERNAL NAME 'ru.example.DBMS_Random.seed(String)'
  	ENGINE JAVA;
  	
	FUNCTION RANDOM () RETURNS INT
  	EXTERNAL NAME 'ru.example.DBMS_Random.random()'
  	ENGINE JAVA;

	FUNCTION NORMAL() RETURNS  DECFLOAT
	  	EXTERNAL NAME 'ru.example.DBMS_Random.normal()'
  	ENGINE JAVA;

	FUNCTION RAND_STRING (
		p_otp CHAR,
		p_len INT
	) RETURNS VARCHAR
  	EXTERNAL NAME 'ru.example.DBMS_Random.string(String, int)'
  	ENGINE JAVA;

  		
	FUNCTION RAND_VALUE (
		p_low DECFLOAT,
		p_high DECFLOAT
	)  RETURNS  DECFLOAT
  	EXTERNAL NAME 'ru.example.DBMS_Random.value(BigDecimal, BigDecimal)'
  	ENGINE JAVA;
END^
SET TERM ; ^
