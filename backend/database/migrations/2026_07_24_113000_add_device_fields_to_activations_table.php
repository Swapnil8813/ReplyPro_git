<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('activations', function (Blueprint $table) {

            $table->string('device_id')->nullable()->after('remarks');

            $table->string('device_name')->nullable()->after('device_id');

            $table->string('app_version')->nullable()->after('device_name');

            $table->timestamp('last_seen')->nullable()->after('app_version');

            $table->boolean('blocked')->default(false)->after('last_seen');

        });
    }

    public function down(): void
    {
        Schema::table('activations', function (Blueprint $table) {

            $table->dropColumn([
                'device_id',
                'device_name',
                'app_version',
                'last_seen',
                'blocked'
            ]);

        });
    }
};