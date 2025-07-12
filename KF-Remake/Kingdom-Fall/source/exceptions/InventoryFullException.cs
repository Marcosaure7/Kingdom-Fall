namespace exceptions;

public class InventoryFullException : KFException
{
    public InventoryFullException() : base("L'inventaire est plein.")
    {
    }

    public InventoryFullException(string message) : base(message)
    {
    }
}