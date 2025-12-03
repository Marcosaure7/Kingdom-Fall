using DotNetEnv;
using MySql.Data.MySqlClient;
namespace App;

public class DatabaseManager
{

    private readonly Stack<MySqlConnection> _connections = new();

    private readonly string _url;

    public DatabaseManager()
    {
        Env.TraversePath().Load();
        _url = $"Server={Env.GetString("DB_HOST")};"
            + $"Port={Env.GetString("DB_PORT")};"
            + $"Database={Env.GetString("DB_NAME")};"
            + $"Uid={Env.GetString("DB_USER")};"
            + $"Pwd={Env.GetString("DB_PASSWORD")};";
    }

    public void NewConnection()
    {
        _connections.Push(new MySqlConnection(_url));
        Console.WriteLine($"Connecting to database with URL.");
    }

    public MySqlDataReader ExecuteQuery(string query)
    {
        return new MySqlCommand(query, _connections.Peek()).ExecuteReader();
    }

    public void OpenConnection()
    {
        NewConnection();
        if (_connections.Peek().State != System.Data.ConnectionState.Open)
        {
            _connections.Peek().Open();
        }
    }

    public void CloseConnection()
    {
        if (_connections.Peek().State == System.Data.ConnectionState.Open)
        {
            _connections.Peek().Close();
        }
    }

    public void CloseAllConnections()
    {
        while (_connections.Count > 0)
        {
            CloseConnection();
            _connections.Pop();
        }
    }
}
