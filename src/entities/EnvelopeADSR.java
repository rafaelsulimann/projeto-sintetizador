package entities;

public class EnvelopeADSR {

    private double attackTime;
    private double decayTime;
    private double releaseTime;

    private double sustainAmplitude;
    private double startAmplitude;
    private double amplitude;

    private double triggerOnTime;
    private double triggerOffTime;


    private boolean noteOn;

    public EnvelopeADSR(){
        this.attackTime = 0.05;
        this.decayTime = 0.3;
        this.startAmplitude = 1.0;
        this.sustainAmplitude = 0;
        this.releaseTime = 0.2;
        this.triggerOnTime = 0.0;
        this.triggerOffTime = 0.0;
        this.noteOn = false;
        this.amplitude = 0.0;
    }

    public EnvelopeADSR(double attackTime, double decayTime, double releaseTime, double sustainAmplitude,
            double startAmplitude, double amplitude, double triggerOnTime, double triggerOffTime, boolean noteOn) {
        this.attackTime = attackTime;
        this.decayTime = decayTime;
        this.releaseTime = releaseTime;
        this.sustainAmplitude = sustainAmplitude;
        this.startAmplitude = startAmplitude;
        this.amplitude = amplitude;
        this.triggerOnTime = triggerOnTime;
        this.triggerOffTime = triggerOffTime;
        this.noteOn = noteOn;
    }

    public double getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(double attackTime) {
        this.attackTime = attackTime;
    }

    public double getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(double decayTime) {
        this.decayTime = decayTime;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(double releaseTime) {
        this.releaseTime = releaseTime;
    }

    public double getSustainAmplitude() {
        return sustainAmplitude;
    }

    public void setSustainAmplitude(double sustainAmplitude) {
        this.sustainAmplitude = sustainAmplitude;
    }

    public double getStartAmplitude() {
        return startAmplitude;
    }

    public void setStartAmplitude(double startAmplitude) {
        this.startAmplitude = startAmplitude;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getTriggerOnTime() {
        return triggerOnTime;
    }

    public void setTriggerOnTime(double triggerOnTime) {
        this.triggerOnTime = triggerOnTime;
    }

    public double getTriggerOffTime() {
        return triggerOffTime;
    }

    public void setTriggerOffTime(double triggerOffTime) {
        this.triggerOffTime = triggerOffTime;
    }

    public boolean isNoteOn() {
        return noteOn;
    }

    public void setNoteOn(boolean noteOn) {
        this.noteOn = noteOn;
    }

    public double getADSAmplitude(double time) {
       double amplitude = 0.0;
       double lifeTime = time - this.triggerOnTime;

       if(this.noteOn){
        // ADS

        // ATTACK
        if(lifeTime < this.attackTime){
            amplitude = (lifeTime / this.attackTime) * this.startAmplitude;
        }
        // DECAY
        else if((lifeTime > this.attackTime && lifeTime <= (this.attackTime + this.decayTime))){
            amplitude = ((lifeTime - this.attackTime) / this.decayTime) * (this.sustainAmplitude - this.startAmplitude) + this.startAmplitude;
        }
        // SUSTAIN
        else if(lifeTime > (this.attackTime + this.decayTime)){
            amplitude = this.sustainAmplitude;
        }
       }
       this.amplitude = amplitude;
       return amplitude;
    }

    public double getReleaseAmplitude(double time) {
       double lifeTime = time - 0.0;

        double amplitude = ((lifeTime / this.releaseTime) * (0 - this.amplitude)) + this.amplitude;
        if (amplitude <= 0.0001)
			amplitude = 0.0;
            
       return amplitude;
    }

    public void noteOn(double timeOn) {
        this.triggerOnTime = timeOn;
        this.noteOn = true;
    }

    public void noteOff(double timeOff) {
        this.triggerOffTime = timeOff;
        this.noteOn = false;
    }

}
