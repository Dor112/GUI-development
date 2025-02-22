import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Car {
    private int tankCapacity;
    private int currentFuel;
    private int desiredFuel;

    public Car(int tankCapacity, int currentFuel, int desiredFuel) {
        this.tankCapacity = tankCapacity;
        this.currentFuel = currentFuel;
        this.desiredFuel = desiredFuel;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    public int getCurrentFuel() {
        return currentFuel;
    }

    public int getDesiredFuel() {
        return desiredFuel;
    }

    public int getFreeSpace() {
        return tankCapacity - currentFuel;
    }
}

class GasStation {
    private int fuelSupply;
    private int columns;
    private Queue<Car> carQueue;
    private int refuelingSpeed = 20;
    private int maxWaitTime = 12;
    private int carsServed = 0;

    public GasStation(int fuelSupply, int columns) {
        this.fuelSupply = fuelSupply;
        this.columns = columns;
        this.carQueue = new LinkedList<>();
    }

    public void addCar(Car car) {
        carQueue.add(car);
        System.out.println("Автомобиль добавлен в очередь. Очередь: " + carQueue.size());
        checkQueue();
    }

    private void checkQueue() {
        while (!carQueue.isEmpty() && columns > 0) {
            Car car = carQueue.poll();
            int requiredFuel = Math.min(car.getDesiredFuel(), car.getFreeSpace());
            if (fuelSupply >= requiredFuel) {
                fuelSupply -= requiredFuel;
                columns--;
                System.out.println("Автомобиль заправляется. Осталось топлива: " + fuelSupply);
                new Thread(() -> {
                    try {
                        int refuelTime = (requiredFuel / refuelingSpeed) * 60;
                        Thread.sleep(refuelTime * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    columns++;
                    carsServed++;
                    System.out.println("Автомобиль заправлен. Свободных колонок: " + columns);
                    checkQueue();
                }).start();
            } else {
                System.out.println("Недостаточно топлива на АЗС.");
                break;
            }
        }


        if (carQueue.size() > 0 && columns == 0) {
            System.out.println("Время ожидания превышает 12 минут. Добавляем новую колонку.");
            columns++;
        }
    }

    public void refillFuel(int amount) {
        fuelSupply += amount;
        System.out.println("Топливо пополнено. Текущий запас: " + fuelSupply);
    }

    public int getCarsServed() {
        return carsServed;
    }
}

class GasStationSimulation {
    public static void main(String[] args) {
        GasStation gasStation = new GasStation(200, 2);
        Random random = new Random();


        for (int i = 0; i < 10; i++) {
            int tankCapacity = 40 + random.nextInt(60);
            int currentFuel = random.nextInt(tankCapacity);
            int desiredFuel = 10 + random.nextInt(30);
            Car car = new Car(tankCapacity, currentFuel, desiredFuel);
            gasStation.addCar(car);

            try {
                Thread.sleep(3000+random.nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        new Thread(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gasStation.refillFuel(500);
        }).start();


        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Итог: обслужено автомобилей - " + gasStation.getCarsServed());
    }
}