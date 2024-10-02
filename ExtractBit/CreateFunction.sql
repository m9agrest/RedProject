CREATE FUNCTION EXTRACT_BIT (
	p_number BIGINT,
	p_index BIGINT
) 
RETURNS BIGINT
AS
BEGIN
    RETURN MOD((p_number / POWER(2, p_index)), 2);
END
