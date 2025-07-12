using DotNetEnv;
using MySql.Data.MySqlClient;
namespace App;

public class DatabaseManager
{

    private readonly Stack<MySqlConnection> Connections = new();

    private string URL;

    public DatabaseManager()
    {
        Env.TraversePath().Load();
        URL = $"Server={Env.GetString("DB_HOST")};"
            + $"Port={Env.GetString("DB_PORT")};"
            + $"Database={Env.GetString("DB_NAME")};"
            + $"Uid={Env.GetString("DB_USER")};"
            + $"Pwd={Env.GetString("DB_PASSWORD")};";
    }

    public void NewConnection()
    {
        Connections.Push(new MySqlConnection(URL));
        Console.WriteLine($"Connecting to database with URL: {URL}");
    }

    public MySqlDataReader ExecuteQuery(string query)
    {
        return new MySqlCommand(query, Connections.Peek()).ExecuteReader();
    }

    public void OpenConnection()
    {
        NewConnection();
        if (Connections.Peek().State != System.Data.ConnectionState.Open)
        {
            Connections.Peek().Open();
        }
    }

    public void CloseConnection()
    {
        if (Connections.Peek() != null && Connections.Peek().State == System.Data.ConnectionState.Open)
        {
            Connections.Peek().Close();
        }
    }

    public void CloseAllConnections()
    {
        while (Connections.Count > 0)
        {
            CloseConnection();
            Connections.Pop();
        }
    }
}
