package interfaces;

public interface IThreadCallback
{
    void onComplete();
    void onCanceled();
    void onUpdate(String message, int progress);
}
