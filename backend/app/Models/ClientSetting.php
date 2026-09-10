<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ClientSetting extends Model
{
    protected $fillable = [

        'activation_id',

        'whatsapp_enabled',

        'whatsapp_message',

        'sms_enabled',

        'sms_message',

        'attachment',

        'duplicate_hours',

        'send_delay'

    ];

    protected $casts = [

        'whatsapp_enabled' => 'boolean',

        'sms_enabled' => 'boolean'

    ];

    public function activation()
    {
        return $this->belongsTo(Activation::class);
    }
}