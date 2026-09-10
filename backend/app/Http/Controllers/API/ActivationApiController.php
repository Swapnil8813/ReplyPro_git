<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\Activation;
use Carbon\Carbon;
use Illuminate\Http\Request;

class ActivationApiController extends Controller
{
    /**
     * Check Activation
     */
    public function check(Request $request)
    {
        if ($request->api_key !== config('replypro.api_key')) {
            return response()->json([
                'status' => false,
                'message' => 'UNAUTHORIZED'
            ], 401);
        }

        $request->validate([
            'mobile_number' => 'required|string',
            'device_id'     => 'required|string',
            'device_name'   => 'required|string',
            'app_version'   => 'required|string',
        ]);

        $activation = Activation::with('client')
            ->where('mobile_number', $request->mobile_number)
            ->first();

        if (!$activation) {
            return response()->json([
                'status' => false,
                'code' => 'NOT_FOUND',
                'message' => 'Activation not found.'
            ], 404);
        }

        if ($activation->blocked) {
            return response()->json([
                'status' => false,
                'code' => 'BLOCKED',
                'message' => 'This activation has been blocked.'
            ], 403);
        }

        if (Carbon::parse($activation->expiry_date)->isPast()) {

            $activation->update([
                'status' => 'Expired'
            ]);

            return response()->json([
                'status' => false,
                'code' => 'EXPIRED',
                'message' => 'Activation expired.'
            ], 403);
        }

        if (empty($activation->device_id)) {

            $activation->update([
                'device_id' => $request->device_id,
                'device_name' => $request->device_name,
                'app_version' => $request->app_version,
                'last_seen' => now(),
            ]);
        }

        if ($activation->device_id !== $request->device_id) {
            return response()->json([
                'status' => false,
                'code' => 'DEVICE_MISMATCH',
                'message' => 'This activation is already registered on another device.'
            ], 403);
        }

        $activation->update([
            'device_name' => $request->device_name,
            'app_version' => $request->app_version,
            'last_seen' => now(),
            'status' => 'Active',
        ]);

        return response()->json([
            'status' => true,
            'message' => 'Activation verified successfully.',

            'client' => [
                'id' => $activation->client->id,
                'code' => $activation->client->client_code,
                'name' => $activation->client->name,
                'mobile' => $activation->client->mobile,
                'plan' => $activation->client->plan,
            ],

            'activation' => [
                'mobile_number' => $activation->mobile_number,
                'start_date' => $activation->start_date,
                'expiry_date' => $activation->expiry_date,
                'status' => $activation->status,
                'whatsapp_enabled' => (bool)$activation->whatsapp_enabled,
                'whatsapp_message' => $activation->whatsapp_message,
                'attachment_type' => $activation->attachment_type,
                'attachment_url' => $activation->whatsapp_attachment,
                'sms_enabled' => (bool)$activation->sms_enabled,
                'sms_message' => $activation->sms_message,
            ],

            'device' => [
                'device_id' => $activation->device_id,
                'device_name' => $activation->device_name,
                'app_version' => $activation->app_version,
                'last_seen' => optional($activation->last_seen)->toDateTimeString(),
            ]
        ]);
    }

    /**
     * Sync Activation
     */
    public function sync(Request $request, $mobile)
    {
        if ($request->api_key !== config('replypro.api_key')) {
            return response()->json([
                'status' => false,
                'message' => 'UNAUTHORIZED'
            ], 401);
        }

        $activation = Activation::with('client')
            ->where('mobile_number', $mobile)
            ->first();

        if (!$activation) {
            return response()->json([
                'status' => false,
                'code' => 'NOT_FOUND',
                'message' => 'Activation not found.'
            ], 404);
        }

        return response()->json([
            'status' => true,

            'activation' => [
                'whatsapp_enabled' => (bool)$activation->whatsapp_enabled,
                'whatsapp_message' => $activation->whatsapp_message,
                'attachment_type' => $activation->attachment_type,
                'attachment_url' => $activation->whatsapp_attachment,
                'sms_enabled' => (bool)$activation->sms_enabled,
                'sms_message' => $activation->sms_message,
                'expiry_date' => $activation->expiry_date,
                'blocked' => (bool)$activation->blocked,
            ]
        ]);
    }
}   