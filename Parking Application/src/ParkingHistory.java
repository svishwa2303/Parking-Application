public class ParkingHistory {
    private String type;
    private int number;
    private int entryDay;
    private int entryMonth;
    private int entryYear;
    private int entryHour;
    private int entryMinute;
    private String entryZone;
    private int exitDay;
    private int exitMonth;
    private int exitYear;
    private int exitHour;
    private int exitMinute;
    private String exitZone;
    private String lot;
    private int parkingDuration;
    private double totalCost;
    public ParkingHistory()
    {}

    public ParkingHistory(String type, int number, int entryDay, int entryMonth, int entryYear, int entryHour, int entryMinute, String entryZone, int exitDay, int exitMonth, int exitYear, int exitHour, int exitMinute, String exitZone, String lot, int parkingDuration, double totalCost) {
        this.type = type;
        this.number = number;
        this.entryDay = entryDay;
        this.entryMonth = entryMonth;
        this.entryYear = entryYear;
        this.entryHour = entryHour;
        this.entryMinute = entryMinute;
        this.entryZone = entryZone;
        this.exitDay = exitDay;
        this.exitMonth = exitMonth;
        this.exitYear = exitYear;
        this.exitHour = exitHour;
        this.exitMinute = exitMinute;
        this.exitZone = exitZone;
        this.lot = lot;
        this.parkingDuration = parkingDuration;
        this.totalCost = totalCost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getEntryDay() {
        return entryDay;
    }

    public void setEntryDay(int entryDay) {
        this.entryDay = entryDay;
    }

    public int getEntryMonth() {
        return entryMonth;
    }

    public void setEntryMonth(int entryMonth) {
        this.entryMonth = entryMonth;
    }

    public int getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(int entryYear) {
        this.entryYear = entryYear;
    }

    public int getEntryHour() {
        return entryHour;
    }

    public void setEntryHour(int entryHour) {
        this.entryHour = entryHour;
    }

    public int getEntryMinute() {
        return entryMinute;
    }

    public void setEntryMinute(int entryMinute) {
        this.entryMinute = entryMinute;
    }

    public String getEntryZone() {
        return entryZone;
    }

    public void setEntryZone(String entryZone) {
        this.entryZone = entryZone;
    }

    public int getExitDay() {
        return exitDay;
    }

    public void setExitDay(int exitDay) {
        this.exitDay = exitDay;
    }

    public int getExitMonth() {
        return exitMonth;
    }

    public void setExitMonth(int exitMonth) {
        this.exitMonth = exitMonth;
    }

    public int getExitYear() {
        return exitYear;
    }

    public void setExitYear(int exitYear) {
        this.exitYear = exitYear;
    }

    public int getExitHour() {
        return exitHour;
    }

    public void setExitHour(int exitHour) {
        this.exitHour = exitHour;
    }

    public int getExitMinute() {
        return exitMinute;
    }

    public void setExitMinute(int exitMinute) {
        this.exitMinute = exitMinute;
    }

    public String getExitZone() {
        return exitZone;
    }

    public void setExitZone(String exitZone) {
        this.exitZone = exitZone;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public int getParkingDuration() {
        return parkingDuration;
    }

    public void setParkingDuration(int parkingDuration) {
        this.parkingDuration = parkingDuration;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
