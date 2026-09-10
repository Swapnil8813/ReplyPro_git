<x-app-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-gray-800 leading-tight">
            {{ __('ReplyPro Dashboard') }}
        </h2>
    </x-slot>

    <div class="py-6">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">

            <div class="grid grid-cols-1 md:grid-cols-4 gap-6">

                <div class="bg-white p-6 rounded-lg shadow">
                    <h3 class="text-gray-500">Customers</h3>
                    <p class="text-3xl font-bold">0</p>
                </div>

                <div class="bg-white p-6 rounded-lg shadow">
                    <h3 class="text-gray-500">Active Licenses</h3>
                    <p class="text-3xl font-bold">0</p>
                </div>

                <div class="bg-white p-6 rounded-lg shadow">
                    <h3 class="text-gray-500">Devices</h3>
                    <p class="text-3xl font-bold">0</p>
                </div>

                <div class="bg-white p-6 rounded-lg shadow">
                    <h3 class="text-gray-500">Templates</h3>
                    <p class="text-3xl font-bold">0</p>
                </div>

            </div>

        </div>
    </div>
</x-app-layout>