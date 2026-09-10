public function up(): void
{
    Schema::create('devices', function (Blueprint $table) {

        $table->id();

        $table->foreignId('license_id')
              ->constrained()
              ->cascadeOnDelete();

        $table->string('device_uid')->unique();

        $table->string('device_name');

        $table->string('computer_name')->nullable();

        $table->string('os_name')->nullable();

        $table->string('os_version')->nullable();

        $table->string('ip_address')->nullable();

        $table->string('mac_address')->nullable();

        $table->timestamp('last_seen')->nullable();

        $table->enum('status',[
            'Active',
            'Blocked'
        ])->default('Active');

        $table->timestamps();
    });
}