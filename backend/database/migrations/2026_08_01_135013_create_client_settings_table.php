<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('client_settings', function (Blueprint $table) {

            $table->id();

            $table->foreignId('activation_id')
                ->constrained('activations')
                ->cascadeOnDelete();

            $table->boolean('whatsapp_enabled')
                ->default(true);

            $table->longText('whatsapp_message')
                ->nullable();

            $table->boolean('sms_enabled')
                ->default(false);

            $table->longText('sms_message')
                ->nullable();

            $table->string('attachment')
                ->nullable();

            $table->timestamps();

        });
    }

    public function down(): void
    {
        Schema::dropIfExists('client_settings');
    }
};