public function up(): void
{
    Schema::create('licenses', function (Blueprint $table) {

        $table->id();

        $table->foreignId('client_id')
              ->constrained()
              ->cascadeOnDelete();

        $table->string('license_key')->unique();

        $table->string('product_name');

        $table->enum('plan',[
            'Basic',
            'Professional',
            'Enterprise'
        ])->default('Basic');

        $table->integer('device_limit')->default(1);

        $table->integer('activated_devices')->default(0);

        $table->date('purchase_date');

        $table->date('expiry_date');

        $table->enum('status',[
            'Active',
            'Expired',
            'Suspended'
        ])->default('Active');

        $table->text('remarks')->nullable();

        $table->timestamps();
    });
}