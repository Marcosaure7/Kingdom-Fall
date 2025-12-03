using Avalonia;
using Avalonia.Controls;
using App;
using Avalonia.Markup.Xaml;
using Avalonia.Controls.ApplicationLifetimes;
using System.Threading.Tasks;

class Program
{
    static void Main(string[] args)
    {
        BuildAvaloniaApp()
            .StartWithClassicDesktopLifetime(args);
    }

    public static AppBuilder BuildAvaloniaApp()
            => AppBuilder.Configure<App>()
                .UsePlatformDetect()
                .LogToTrace();


    public class App : Application
    {
        private GameLogic? _gameLogic;
        public override async void OnFrameworkInitializationCompleted()
        {
            if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
            {
                desktop.MainWindow = new controllers.FenetreAppController();
                if (!Design.IsDesignMode)
                {
                    _gameLogic = new GameLogic(desktop.MainWindow as controllers.FenetreAppController);
                    await _gameLogic.Run();
                }
            }
            base.OnFrameworkInitializationCompleted();
        }
    }
}
