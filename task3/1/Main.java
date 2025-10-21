class Main{
    public static void main(String[] args){
        int random = (new java.util.Random()).nextInt(900) + 100;
        int sum = 0;
        System.out.println("\nСлучайное трехзначное число:" + random);
        while (random > 0) { 
            sum = sum + random % 10;
            random = random / 10;
        }
        System.out.println("Сумма его цифр = " + sum + "\n");
        
    }
}