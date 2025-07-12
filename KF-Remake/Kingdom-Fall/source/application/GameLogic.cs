namespace App;

using System.Threading.Tasks;

public class GameLogic
{
    public Jeu JeuEnCours;
    private TaskCompletionSource<bool>? _latchFinTour;
    private readonly controllers.FenetreAppController _controller;

    public GameLogic(controllers.FenetreAppController controller)
    {
        _controller = controller;
        _controller.SetThread(this);
        JeuEnCours = new Jeu(controller, this);
    }

    public async Task Run()
    {
        await Task.Run(() => JeuEnCours.LancerJeu());
    }

    public async Task AttendreFinTour()
    {
        _latchFinTour = new TaskCompletionSource<bool>();
        await _latchFinTour.Task;
    }

    public void RelacherLatch()
    {
        if (_latchFinTour is not null && !_latchFinTour.Task.IsCompleted)
        {
            _latchFinTour?.SetResult(true);
        }
    }

    public void Attaque()
    {
        Task.Run(() => JeuEnCours.EffectuerAttaque());
    }

    public void Ramasser()
    {
        try
        {
            JeuEnCours.Ramasser();
            _controller.DissiperDrop();
        }
        catch (exceptions.InventoryFullException)
        {
            _controller.InventairePlein();
        }
    }

    public void UpdateUsername(string playerName)
    {
        JeuEnCours.UpdateUsername(playerName);
    }

    public void Equiper()
    {
        try
        {
            JeuEnCours.Equiper();
            _controller.DissiperDrop();
        }
        catch (exceptions.InventoryFullException)
        {
            _controller.InventairePlein();
        }
    }
}
