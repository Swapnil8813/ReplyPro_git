<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
       Schema::create('customers', function (Blueprint $table) {
    $table->id();

    $table->string('name');
    $table->string('mobile',15)->unique();
    $table->string('email')->nullable();

    $table->string('token_number')->unique();

    $table->foreignId('license_id')->nullable();

    $table->string('device_id')->nullable();

    $table->enum('status',[
        'active',
        'inactive',
        'expired',
        'blocked'
    ])->default('active');

    $table->timestamp('last_login')->nullable();

    $table->timestamps();
});
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('customers');
    }
};
