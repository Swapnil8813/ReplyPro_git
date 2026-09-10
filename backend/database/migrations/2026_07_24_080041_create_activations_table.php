<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('activations', function (Blueprint $table) {

            $table->id();

            $table->foreignId('client_id')
                ->constrained()
                ->cascadeOnDelete();

            $table->string('mobile_number');

            $table->date('start_date');

            $table->date('expiry_date');

            $table->enum('status', [
                'Active',
                'Expired'
            ])->default('Active');

            // WhatsApp

            $table->boolean('whatsapp_enabled')->default(true);

            $table->longText('whatsapp_message')->nullable();

            $table->string('whatsapp_attachment')->nullable();

            $table->enum('attachment_type', [
                'image',
                'video',
                'pdf'
            ])->nullable();

            // SMS

            $table->boolean('sms_enabled')->default(true);

            $table->longText('sms_message')->nullable();

            // Notes

            $table->text('remarks')->nullable();

            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('activations');
    }
};